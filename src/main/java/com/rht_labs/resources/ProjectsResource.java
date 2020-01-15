package com.rht_labs.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.rht_labs.services.GitLabService;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectsResource {

    @Inject
    @RestClient
    private GitLabService gitLabService;

    @GET
    public String list() {
        // TODO - connect this to our REST Client
        return gitLabService.getProjects();
    }
}