package com.redhat.labs.lodestar.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.redhat.labs.lodestar.models.gitlab.HookConfig;
import com.redhat.labs.lodestar.service.ConfigService;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigResource {

    @Inject
    ConfigService configService;
    
    @GET
    @Path("/v2/config/webhooks")
    public Response getWebhooks() {
        List<HookConfig> hooks = configService.getHookConfig();        
        return Response.ok(hooks).build();
    }
}
