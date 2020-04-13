package com.redhat.labs.omp.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import com.redhat.labs.omp.models.Version;

/**
 * Provides version information via api. Expected to come from the container in a prod env
 * @author mcanoy
 *
 */
@Path("/api/v1/version")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VersionResource {

    @ConfigProperty(name = "git.commit")
    String gitCommit;
    
    @ConfigProperty(name = "git.tag")
    String gitTag;
    
    @GET
    @Timed(name="versionResourceTimer")
    @Counted(name="versionResourceCounter")
    public Version getVersion() {
        return new Version(gitCommit, gitTag);
    }
}
