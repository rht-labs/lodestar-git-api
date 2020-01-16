package com.rht_labs.omp.services;

import com.rht_labs.omp.models.CreateProjectRequest;
import com.rht_labs.omp.models.GitLabCreateFileInRepositoryRequest;
import com.rht_labs.omp.models.GitLabCreateProjectRequest;
import com.rht_labs.omp.models.GitLabCreateProjectResponse;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/api/v4")
@RegisterRestClient(configKey = "gitlab.api")
@ClientHeaderParam(name = "Private-Token", value = "{com.rht_labs.omp.config.GitLabConfig.getPersonalAccessToken}")
public interface GitLabService {
    // reference: https://docs.gitlab.com/ee/api/projects.html#list-all-projects
    @GET
    @Path("/projects")
    @Produces("application/json")
    Response getProjects();

    // reference: https://docs.gitlab.com/ee/api/projects.html#create-project
    @POST
    @Path("/projects")
    @Produces("application/json")
    GitLabCreateProjectResponse createNewProject(GitLabCreateProjectRequest request);

    // reference: https://docs.gitlab.com/ee/api/repository_files.html#create-new-file-in-repository
    @POST
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    Response createFileInRepository(@PathParam("id") Integer projectId, @PathParam("file_path") String filePath, GitLabCreateFileInRepositoryRequest request);
}