package com.redhat.labs.omp.resources;

import com.redhat.labs.omp.models.gitlab.request.CreateProjectRequest;
import com.redhat.labs.omp.models.gitlab.request.GitLabCreateProjectRequest;
import com.redhat.labs.omp.models.gitlab.response.GitLabCreateProjectResponse;
import com.redhat.labs.omp.rest.client.GitLabService;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectsResource {
    @Inject
    @RestClient
    protected GitLabService gitLabService;

    @ConfigProperty(name = "residenciesRepoId", defaultValue = "3060")
    protected Integer residenciesRepoId;

    @ConfigProperty(name = "residenciesParentRepositoryId", defaultValue = "6284")
    protected Integer residenciesParentRepositoryId;

    @ConfigProperty(name = "deployKey")
    protected Integer deployKey;

    @GET
    public String listAllProjects() {
        return gitLabService.getProjects().readEntity(String.class);
    }

    @DELETE
    @Path("{project_id}")
    public String deleteProject(@PathParam("project_id") String projectId) {
        return gitLabService.deleteProject(projectId).readEntity(String.class);
    }

    @POST
    public GitLabCreateProjectResponse createNewProject(CreateProjectRequest request) {
        GitLabCreateProjectRequest gitLabRequest = new GitLabCreateProjectRequest();
        gitLabRequest.name = request.projectName;
        gitLabRequest.namespace_id = residenciesRepoId;
        GitLabCreateProjectResponse gitLabProject = gitLabService.createNewProject(gitLabRequest);
        gitLabService.enableDeployKey(gitLabProject.id, deployKey);
        return gitLabProject;
    }
}