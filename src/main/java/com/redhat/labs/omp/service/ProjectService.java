package com.redhat.labs.omp.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.omp.models.PagedResults;
import com.redhat.labs.omp.models.gitlab.Commit;
import com.redhat.labs.omp.models.gitlab.DeployKey;
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
    
    @ConfigProperty(name = "commit.page.size")
    int commitPageSize;

    // get a project - this could be a replaced by a direct call to the project via the path
    // but must consider changes to the path for special characters
    public Optional<Project> getProjectByName(Integer namespaceId, String name) {

        Optional<Project> optional = Optional.empty();
        
        PagedResults<ProjectSearchResults> page = new PagedResults<>();
        
        while(page.hasMore()) {
            Response response = gitLabService.getProjectByName(name, commitPageSize, page.getNumber());
            page.update(response, new GenericType<List<ProjectSearchResults>>() {});
        }

        // look for a project with name that matches the namespace id and the path
        for (ProjectSearchResults result : page.getResults()) {
            LOGGER.debug("PSR {} = {} {}  =  {}", namespaceId, result.getNamespace().getId(), name, result.getPath());
            if (namespaceId.equals(result.getNamespace().getId()) && name.equals(result.getPath())) {
                return Optional.of(Project.from(result));
            }
        }

        return optional;

    }
    
    public List<Project> getProjectsByGroup(int groupId, Boolean includeSubgroups) {
        
        PagedResults<Project> page = new PagedResults<>();
        while(page.hasMore()) {
            Response response = gitLabService.getProjectsbyGroup(groupId, includeSubgroups, commitPageSize, page.getNumber());
            page.update(response, new GenericType<List<Project>>() {});
        }
        
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("project count group id ({}) {}", groupId, page.size());
            page.getResults().stream().forEach(project -> LOGGER.debug("Project {}", project.getPathWithNamespace()));
        }
        
        return page.getResults();
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

    // enable deployment key - by default it's ready only but we need to write so let's 2-step
    public void enableDeploymentKeyOnProject(Integer projectId, Integer deployKey) {
        
        gitLabService.enableDeployKey(projectId, deployKey);
        gitLabService.updateDeployKey(projectId, deployKey, DeployKey.builder().title("LodeStar DK").canPush(true).build());
    }
    
    public List<Commit> getCommitLog(String projectId) {
        PagedResults<Commit> page = new PagedResults<>();
        
        while(page.hasMore()) {
            Response response = gitLabService.getCommitLog(projectId, commitPageSize, page.getNumber());
            page.update(response, new GenericType<List<Commit>>() {});
        }
        
        LOGGER.debug("total commits for project {} {}", projectId, page.size());
          
        return page.getResults();
    }

}
