package com.redhat.labs.omp.resources;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.resources.filters.Logged;
import com.redhat.labs.omp.service.FileService;

@Path("/api/file")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LegacyFileResource {

    @Inject
    FileService fileService;

    @ConfigProperty(name = "file_branch", defaultValue = "master")
    protected String defaultBranch;

    @GET
    @Logged
    public Response getFileFromGitByName(@QueryParam("name") String fileName, @QueryParam("repo_id") Integer repoId,
            @QueryParam("branch") String branch) {
        Optional<File> optional = fileService.getFile(repoId, fileName, branch);
        if (optional.isEmpty()) {
            return Response.status(HttpStatus.SC_NOT_FOUND).build();
        }

        return Response.status(HttpStatus.SC_OK).entity(optional.get()).build();

    }

}
