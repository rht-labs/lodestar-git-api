package com.redhat.labs.omp.resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.service.ConfigService;

@Path("/api/v1/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigResource {

    @Inject
    ConfigService configService;

    @GET
    public File get() {
        return configService.getConfigFile();
    }

}
