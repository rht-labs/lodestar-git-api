package com.redhat.labs.omp.resource;

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
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.service.ConfigService;

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
        return configService.getConfigFile();
    }

    @GET
    @Path("/v2/config")
    public Response getJson() {
        File configFile = configService.getConfigFile();
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        try {
            LOGGER.debug(configFile.getContent());
            Object content =  om.readValue(configFile.getContent(), Object.class);
            return Response.ok(content).build();
        } catch (JsonProcessingException e) {
            LOGGER.error(String.format("Error processing config file %s", configFile.getFilePath()), e);
            return Response.serverError().build();
        }
    }
}
