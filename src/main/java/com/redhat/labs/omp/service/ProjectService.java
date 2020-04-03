package com.redhat.labs.omp.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.redhat.labs.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.omp.models.gitlab.Project;
import com.redhat.labs.omp.models.gitlab.ProjectSearchResults;
import com.redhat.labs.omp.rest.client.GitLabService;

@ApplicationScoped
public class ProjectService {

    @Inject
    @RestClient
    GitLabService gitLabService;

    // get a project
    public Optional<Project> getProjectByName(Integer namespaceId, String name)
            throws UnexpectedGitLabResponseException {

        Optional<Project> optional = Optional.empty();

        List<ProjectSearchResults> resultList = gitLabService.getProjectByName(name);

        if (null == resultList || resultList.isEmpty()) {
            return optional;
        }

        if (1 == resultList.size()) {
            return Optional.of(Project.from(resultList.get(0)));
        }

        // found more than one project with name in either 'name' or 'path' attribute
        // should match path
        // TODO: This is checking parent id here. can this be part of the search?
        for (ProjectSearchResults result : resultList) {
            if (namespaceId.equals(result.getNamespace().getId()) && name.equalsIgnoreCase(result.getPath())) {
                return Optional.of(Project.from(result));
            }
        }

        return optional;

    }

    public Optional<Project> getProjectById(Integer projectId) {

        Optional<Project> optional = Optional.empty();

        Project project = gitLabService.getProjectById(projectId);

        if (null != project) {
            optional = Optional.of(project);
        }

        return optional;

    }

    // create a project
    public Optional<Project> createProject(Project project) {

        Optional<Project> optional = Optional.empty();

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
