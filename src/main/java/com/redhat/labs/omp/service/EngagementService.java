package com.redhat.labs.omp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.redhat.labs.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.gitlab.Action;
import com.redhat.labs.omp.models.gitlab.CommitMultiple;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.models.gitlab.FileAction;
import com.redhat.labs.omp.models.gitlab.Group;
import com.redhat.labs.omp.models.gitlab.Project;

@ApplicationScoped
public class EngagementService {

    private static final String ENGAGEMENT_PROJECT_NAME = "iac";
    private final String COMMIT_MSG = "\uD83E\uDD84 Created by OMP Git API \uD83D\uDE80 \uD83C\uDFC1";
    private final String DEFAULT_BRANCH = "master";

    @ConfigProperty(name = "residenciesParentRepositoryId")
    Integer engagementParentId;

    @ConfigProperty(name = "stripPathPrefix", defaultValue = "schema/")
    String stripPathPrefix;

    @ConfigProperty(name = "deployKey")
    Integer deployKey;

    @Inject
    ProjectService projectService;

    @Inject
    GroupService groupService;

    @Inject
    TemplateService templateService;

    @Inject
    FileService fileService;

    // create an engagement
    public Project createEngagement(Engagement engagement) {

        // create project structure
        Project project = createProjectStucture(engagement);

        // get all template files
        List<File> templateFiles = templateService.getAllFilesFromTemplateInventory();

        // process templates against engagement
        templateService.processTemplatesForEngagement(templateFiles, engagement);

        // create actions for multiple commit
        CommitMultiple commit = createCommitMultiple(templateFiles, project.getId(), DEFAULT_BRANCH);

        // send commit to gitlab
        if (!fileService.createFiles(project.getId(), commit)) {
            throw new UnexpectedGitLabResponseException("failed to commit files for engagement creation.");
        }

        return project;

    }

    private Project createProjectStucture(Engagement engagement) {

        // create group for customer name
        Group customerGroup = getOrCreateGroup(engagement.getCustomerName(),
                Group.builder().name(engagement.getCustomerName()).path(engagement.getCustomerName())
                        .parentId(engagementParentId).build());

        // create group for project name
        Group projectGroup = getOrCreateGroup(engagement.getProjectName(),
                Group.builder().name(engagement.getProjectName()).path(engagement.getProjectName())
                        .parentId(customerGroup.getId()).build());

        // create project under project name group
        Project project = getOrCreateProject(projectGroup.getId(), ENGAGEMENT_PROJECT_NAME,
                Project.builder().name(ENGAGEMENT_PROJECT_NAME).namespaceId(projectGroup.getId()).build());

        // enable deployment key on project
        projectService.enableDeploymentKeyOnProject(project.getId(), deployKey);

        return project;

    }

    private Group getOrCreateGroup(String groupName, Group groupToCreate) {

        Optional<Group> optional = groupService.getGitLabGroupByName(groupName);

        if (optional.isEmpty()) {

            // try to create group
            optional = groupService.createGitLabGroup(groupToCreate);

            if (optional.isEmpty()) {
                throw new UnexpectedGitLabResponseException("failed to create group");
            }

        }

        return optional.get();

    }

    private Project getOrCreateProject(Integer namespaceId, String projectName, Project project) {

        Optional<Project> optional = projectService.getProjectByName(namespaceId, projectName);

        if (optional.isEmpty()) {

            // try to create project
            optional = projectService.createProject(project);

            if (optional.isEmpty()) {
                throw new UnexpectedGitLabResponseException("failed to create project");
            }

        }

        return optional.get();

    }

    private CommitMultiple createCommitMultiple(List<File> filesToCommit, Integer projectId, String branch) {

        List<Action> actions = new ArrayList<>();

        // convert each file to action
        filesToCommit.parallelStream().forEach(file -> actions.add(createAction(file, FileAction.create)));

        return CommitMultiple.builder().id(projectId).branch(branch).commitMessage(COMMIT_MSG).actions(actions).build();

    }

    private Action createAction(File file, FileAction action) {
        return Action.builder().action(action).filePath(stripPrefix(file.getFilePath())).content(file.getContent())
                .encoding("base64").build();
    }

    private String stripPrefix(String in) {
        if (in != null && in.startsWith(stripPathPrefix)) {
            return in.split(stripPathPrefix)[1];
        }
        return in;
    }

    // update an engagement

    // get an engagement

    // delete an engagement?

}
