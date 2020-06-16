package com.redhat.labs.omp.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.omp.models.gitlab.Project;
import com.redhat.labs.omp.models.gitlab.ProjectSearchResults;
import com.redhat.labs.omp.rest.client.GitLabService;

@ApplicationScoped
public class ProjectService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    @Inject
    @RestClient
    GitLabService gitLabService;

    @ConfigProperty(name = "engagements.do.not.delete")
    boolean doNotDelete;

    // get a project
    public Optional<Project> getProjectByName(Integer namespaceId, String name) {

        Optional<Project> optional = Optional.empty();

        List<ProjectSearchResults> resultList = gitLabService.getProjectByName(name);

        if (null == resultList || resultList.isEmpty()) {
            return optional;
        }

        // look for a project with name that matches the namespace id and the path
        for (ProjectSearchResults result : resultList) {
            if (namespaceId.equals(result.getNamespace().getId()) && name.equals(result.getPath())) {
                return Optional.of(Project.from(result));
            }
        }

        return optional;

    }

    //Needed anymore?
    public List<ProjectSearchResults> getAllProjectsByName(String name) {
        return gitLabService.getProjectByName(name);
    }

    public List<Project> getProjectsByGroup(int groupId, Boolean includeSubgroups) {
        List<Project> projects = gitLabService.getProjectsbyGroup(groupId, includeSubgroups);

        if(LOGGER.isDebugEnabled()) {
            LOGGER.trace("project count group id({}) {}", groupId, projects.size());
            projects.stream().forEach(project -> LOGGER.debug("Group {} Project {}", groupId, project.getName()));
        }

        return projects;
    }

    public Optional<Project> getProjectById(Integer projectId) {
        return getProjectByIdOrPath(String.valueOf(projectId));
    }
        
    public Optional<Project> getProjectByIdOrPath(String idOrPath) {
        Project project = gitLabService.getProjectById(idOrPath);
        return Optional.ofNullable(project);
    }
    
    // create a project
    public Optional<Project> createProject(Project project) {

        Optional<Project> optional = Optional.empty();

        if(doNotDelete) {
            project.preserve();
        }

        LOGGER.debug("create project {}", project);

        // try to create project
        Project createdProject = gitLabService.createProject(project);
        if (null != createdProject) {
            optional = Optional.of(createdProject);
        }

        return optional;

    }

    // update a project
    public Optional<Project> updateProject(Integer projectId, Project project) {

        Optional<Project> optional = Optional.empty();

        // try to update the project
        Project updatedProject = gitLabService.updateProject(projectId, project);
        if (null != updatedProject) {
            optional = Optional.of(updatedProject);
        }

        return optional;

    }

    // delete a project
    public void deleteProject(Integer projectId) {
        gitLabService.deleteProjectById(projectId);
    }

    // enable deployment key
    public void enableDeploymentKeyOnProject(Integer projectId, Integer deployKey) {
        gitLabService.enableDeployKey(projectId, deployKey);
    }

}
