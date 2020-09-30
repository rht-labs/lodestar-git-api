package com.redhat.labs.lodestar.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.ProjectStructure;
import com.redhat.labs.lodestar.models.ProjectStructure.ProjectStructureBuilder;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Namespace;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.utils.GitLabPathUtils;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;

@ApplicationScoped
public class ProjectStructureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStructure.class);

    private static final String ENGAGEMENT_PROJECT_NAME = "iac";
    private static final String CLEANUP_EVENT = "cleanup.project.structure.event";

    @ConfigProperty(name = "engagements.repository.id")
    Integer engagementRepositoryId;

    @Inject
    GroupService groupService;

    @Inject
    ProjectService projectService;

    @Inject
    EventBus eventBus;

    public Project createOrUpdateProjectStructure(Engagement engagement, String engagementPathPrefix) {

        Instant begin = Instant.now();

        // get the existing structure if not new
        ProjectStructure existingProjectStructure = getExistingProjectStructure(engagement, engagementPathPrefix);

        // create or update customer group
        Group customerGroup = processNameChange(engagement.getCustomerName(), engagementRepositoryId,
                existingProjectStructure.getCustomerGroup(), existingProjectStructure.getCustomerGroupHasSubgroups());

        // create or update project group
        Group projectGroup = processNameChange(engagement.getProjectName(), customerGroup.getId(),
                existingProjectStructure.getProjectGroup(), false);

        Optional<Project> project = createOrUpdateProject(existingProjectStructure.getProject(),
                existingProjectStructure.getProjectGroupId(), projectGroup.getId());

        // clean up groups if project moved
        eventBus.sendAndForget(CLEANUP_EVENT, existingProjectStructure);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("create or update project structure took {} ms",
                    Duration.between(begin, Instant.now()).toMillis());
            ;
        }

        return project
                .orElseThrow(() -> new WebApplicationException("failed to create or update project structure", 500));

    }

    ProjectStructure getExistingProjectStructure(Engagement engagement, String engagementPathPrefix) {

        ProjectStructureBuilder builder = ProjectStructure.builder();
        Optional<Project> project = Optional.empty();

        if (0 == engagement.getProjectId()) {
            // if projectId is null, double check using name
            project = findProjectByPath(engagementPathPrefix, engagement.getCustomerName(),
                    engagement.getProjectName());

            if (project.isEmpty()) {
                return builder.build();
            }
        } else {
            // get project group by id
            project = projectService.getProjectById(engagement.getProjectId());
        }

        builder.project(project);

        // get project group
        Namespace namespace = project.filter(p -> null != p.getNamespace()).map(p -> p.getNamespace()).orElse(null);

        Optional<Integer> projectGroupId = Optional.ofNullable(namespace.getId());
        builder.projectGroupId(projectGroupId);
        if (projectGroupId.isPresent()) {
            builder.projectGroup(groupService.getGitLabGroupByById(projectGroupId.get()));
        }

        Optional<Integer> customerGroupId = Optional.ofNullable(namespace.getParentId());
        builder.customerGroupId(customerGroupId);
        if (customerGroupId.isPresent()) {

            builder.customerGroup(groupService.getGitLabGroupByById(customerGroupId.get()));

            // determine if customer group has subgroups other than this project
            List<Group> customerSubgroups = groupService.getSubgroups(customerGroupId.get());
            if (customerSubgroups.size() > 1) {
                builder.customerGroupHasSubgroups(Boolean.TRUE);
            }

        }

        return builder.build();

    }

    Group processNameChange(String newName, Integer parentId, Optional<Group> group, boolean hasSubgroups) {

        Optional<Group> newGroup = Optional.empty();

        if (group.isPresent()) {

            Group existingGroup = group.get();

            if (parentId.equals(group.get().getParentId())) {

                // skip if name has not changed
                if (newName.equals(existingGroup.getName())) {
                    return existingGroup;
                }

                // get new group name
                newGroup = getGroupByName(newName, parentId);

                // update if group has no subgroups
                if (newGroup.isEmpty() && !hasSubgroups) {

                    // set new name/path
                    Group modified = Group.builder().id(existingGroup.getId()).name(newName)
                            .path(GitLabPathUtils.generateValidPath(newName)).parentId(existingGroup.getParentId())
                            .build();

                    // udpate group in Git
                    return groupService.updateGitLabGroup(existingGroup.getId(), modified).orElseThrow(
                            () -> new WebApplicationException("failed to update group name/path for " + group, 500));

                }

            }

        }

        // create group if no existing group or if existing group has subgroups
        return newGroup.isPresent() ? newGroup.get() : getOrCreateGroup(newName, parentId);

    }

    Group createGroup(String name, Integer parentId) {

        return groupService
                .createGitLabGroup(Group.builder().name(name).path(GitLabPathUtils.generateValidPath(name))
                        .parentId(parentId).build())
                .orElseThrow(() -> new WebApplicationException("failed to create group for " + name, 500));

    }

    Optional<Group> getGroupByName(String name, Integer parentId) {
        return groupService.getSubgroups(parentId).stream()
                .filter(g -> g.getName().equals(name) && g.getParentId().equals(parentId)).findFirst();
    }

    Group getOrCreateGroup(String name, Integer parentId) {
        return getGroupByName(name, parentId).orElseGet(() -> createGroup(name, parentId));
    }

    Optional<Project> createOrUpdateProject(Optional<Project> project,
            Optional<Integer> existingParentIdOptional, Integer parentId) {

        if (project.isEmpty()) {
            return createProject(parentId);
        }

        Integer newParentId = (null == parentId) ? -1 : parentId;
        Integer existingParentId = existingParentIdOptional.orElse(-1);

        // no changes required if the parent group hasn't changed
        if (newParentId.equals(existingParentId)) {
            return project;
        }

        Project toMove = project.get();
        Integer projectId = toMove.getId();
        toMove.setMoved(true);

        // move project to new parent/group id
        return projectService.transferProject(projectId, newParentId);

    }

    Optional<Project> createProject(Integer parentId) {

        return projectService.createProject(
                Project.builder().name(ENGAGEMENT_PROJECT_NAME).visibility("private").namespaceId(parentId).build());

    }

    void cleanupGroups(ProjectStructure existingProjectStructure) {

        // do nothing if project missing or has not been moved
        if (existingProjectStructure.getProject().isEmpty() || !existingProjectStructure.getProject().get().isMoved()) {
            return;
        }

        Instant begin = Instant.now();
        // remove project group
        removeGroupIfEmpty(existingProjectStructure.getProjectGroupId().get(), 5);
        // remove customer group
        removeGroupIfEmpty(existingProjectStructure.getCustomerGroupId().get(), 5);

        System.out.println("cleanup elapsed time: " + Duration.between(begin, Instant.now()).toMillis() + " ms");

    }

    void removeGroupIfEmpty(Integer groupId, int retryCount) {

        int count = 0;

        while (count <= retryCount) {

            try {

                // remove if no subgroups or projects
                if (projectService.getProjectsByGroup(groupId, false).isEmpty()
                        && groupService.getSubgroups(groupId).isEmpty()) {
                    groupService.deleteGroup(groupId);
                }

                groupService.getGitLabGroupByById(groupId);

            } catch (WebApplicationException wae) {
                if (wae.getResponse().getStatus() == 404) {
                    break;
                }
            }

            count += 1;
            try {
                System.out.println("sleeping for " + count * 1 + " seconds.");
                TimeUnit.SECONDS.sleep(count * 1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    Optional<Project> findProjectByPath(String engagementPathPrefix, String customerName, String projectName) {

        String customerPath = GitLabPathUtils.generateValidPath(customerName);
        String projectPath = GitLabPathUtils.generateValidPath(projectName);
        String fullPath = GitLabPathUtils.getPath(engagementPathPrefix, customerPath, projectPath);

        return projectService.getProjectByIdOrPath(fullPath);

    }

    @ConsumeEvent(value = CLEANUP_EVENT, blocking = true)
    void consumeCleanupProjectStructureEvent(ProjectStructure existingProjectStructure) {
        cleanupGroups(existingProjectStructure);
    }

}
