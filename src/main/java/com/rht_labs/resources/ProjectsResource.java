package com.rht_labs.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.rht_labs.models.CreateProject;
import com.rht_labs.services.GitLabService;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectsResource {

    private static final CreateProject createProject = null;
    @Inject
    @RestClient
    private GitLabService gitLabService;

    // TODO - add query string to filter by thing eg region, age etc
    @GET
    public Object listAllProjects() {
        return gitLabService.getProjects().getEntity();
    }

    @POST
    public Object createNewProject(CreateProject body) {
        // edit and strip out just the name field

        String awesomeRequestObject = "{\"name\":\"" + body.name + "\"}";

        return gitLabService.createNewProject(awesomeRequestObject).getEntity();
    }
}