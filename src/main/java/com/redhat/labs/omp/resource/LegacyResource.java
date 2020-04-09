package com.redhat.labs.omp.resource;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.models.gitlab.Project;
import com.redhat.labs.omp.service.EngagementService;
import com.redhat.labs.omp.service.FileService;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LegacyResource {

    /*
     * 
     * 
     * NOTE:  This resource should be removed once the OMP Backend API 
     *        has been updated to point to the /api/v1/* endpoints
     * 
     * 
     */

    @Inject
    FileService fileService;

    @Inject
    EngagementService engagementService;

    @ConfigProperty(name = "file_branch", defaultValue = "master")
    protected String defaultBranch;

    @GET
    @Counted(name = "performedGetFile", description = "How many files retrievals have been performed")
    @Timed(name = "getFileTimer", description = "A measuer of how long it takes to perform file retrieval")
    @Path("/file")
    public Response getFileFromGitByName(@QueryParam("name") String fileName, @QueryParam("repo_id") Integer repoId,
            @QueryParam("branch") String branch) {
        Optional<File> optional = fileService.getFile(repoId, fileName, (null == branch) ? defaultBranch : branch);
        if (!optional.isPresent()) {
            return Response.status(HttpStatus.SC_NOT_FOUND).build();
        }

        return Response.status(HttpStatus.SC_OK).entity(optional.get()).build();

    }

    @POST
    @Path("/residencies")
    public Response createEngagement(Engagement engagement, @Context UriInfo uriInfo) {

        Project project = engagementService.createEngagement(engagement);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(project.getId()));
        return Response.created(builder.build()).build();

    }

}
