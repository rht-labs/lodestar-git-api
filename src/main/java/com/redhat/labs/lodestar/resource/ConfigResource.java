package com.redhat.labs.lodestar.resource;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.HookConfig;
import com.redhat.labs.lodestar.service.ConfigService;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigResource.class);

    @Inject
    ConfigService configService;

    @GET
    @Path("/v1/config")
    public File get() {
        LOGGER.info("V1 or undefined is deprecated");
        return configService.getConfigFile();
    }

    @GET
    @Path("/v2/config")
    public Response getJson() {
        File configFile = configService.getConfigFile();
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        try {
            LOGGER.trace(configFile.getContent());
            Object content =  om.readValue(configFile.getContent(), Object.class);
            return Response.ok(content).build();
        } catch (JsonProcessingException e) {
            LOGGER.error(String.format("Error processing config file %s", configFile.getFilePath()), e);
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("/v2/config/webhooks")
    public Response getWebhooks() {
        List<HookConfig> hooks = configService.getHookConfig();
        
        return Response.ok(hooks).build();
    }
}
