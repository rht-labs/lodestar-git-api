package com.rht_labs.services;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.rht_labs.models.CreateProjectResponse;

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
    CreateProjectResponse createNewProject (Object createProject);


    @POST
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    // passthrough so no need  
    Response editFileInRepo (@PathParam Integer id, 
                            @PathParam String file_path, 
                            Object configurationToWriteToGitLabAndSomeOtherStuff);

}