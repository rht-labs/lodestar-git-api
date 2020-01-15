package com.rht_labs.services;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/api/v4")
@RegisterRestClient(configKey="gitlab_service")
@ClientHeaderParam(name="Private-Token", value="{com.rht_labs.config.GitLabConfig.getGitLabPersonalAccessToken}")
public interface GitLabService {

    // TODO add auth as header or query param?

    @GET
    @Path("/projects")
    @Produces("application/json")
    // passthrough so no need  
    String getProjects ();
}