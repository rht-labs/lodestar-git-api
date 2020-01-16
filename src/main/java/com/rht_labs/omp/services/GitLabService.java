package com.rht_labs.omp.services;

import com.rht_labs.omp.models.CreateProjectResponse;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/api/v4")
@RegisterRestClient(configKey = "gitlab_service")
@ClientHeaderParam(name = "Private-Token", value = "{com.rht_labs.omp.config.GitLabConfig.getPersonalAccessToken}")
public interface GitLabService {
    @GET
    @Path("/projects")
    @Produces("application/json")
    Response getProjects();

    @POST
    @Path("/projects")
    @Produces("application/json")
    CreateProjectResponse createNewProject(Object createProject);


    @POST
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    Response editFileInRepo(@PathParam Integer id, @PathParam String file_path, Object configurationToWriteToGitLabAndSomeOtherStuff);
}