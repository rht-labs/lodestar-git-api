package com.rht_labs.services;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.rht_labs.models.CreateProject;

@Path("/api/v4")
@RegisterRestClient(configKey="gitlab_service")
@ClientHeaderParam(name="Private-Token", value="{getMyGitToken}")
public interface GitLabService {

    // TODO - is this a hack? 
    default String getMyGitToken(){
        return System.getenv().get("GITLAB_TOKEN");
    }
    // TODO add auth as header or query param?

    @GET
    @Path("/projects")
    @Produces("application/json")
    // passthrough so no need  
    Response getProjects ();

    @POST
    @Path("/projects")
    @Produces("application/json")
    // passthrough so no need  
    Response createNewProject (Object createProject);
}