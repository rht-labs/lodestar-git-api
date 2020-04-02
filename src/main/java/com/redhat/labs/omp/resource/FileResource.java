package com.redhat.labs.omp.resource;

import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import com.redhat.labs.omp.models.gitlab.CommitMultiple;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.service.FileService;

@Path("/api/v1/projects/{projectId}/files")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FileResource {

    @Inject
    FileService fileService;

    @POST
    public Response post(@PathParam("projectId") Integer projectId, @Valid File file) {

        Optional<File> optional = fileService.createFile(projectId, file.getFilePath(), file);

        if (optional.isPresent()) {
            return Response.status(HttpStatus.SC_CREATED).entity(optional.get()).build();
        }

        return Response.serverError().build();

    }

    @POST
    @Path("/commit/multiple")
    public Response postMultiple(@PathParam("projectId") Integer projectId, @Valid CommitMultiple commit) {

        Response response = Response.serverError().build();

        try {
            if(fileService.createFiles(projectId, commit)) {
                response = Response.status(HttpStatus.SC_CREATED).entity(commit).build();
            }
        } catch(Exception e) {
            // return server error
        }

        return response;

    }

    @PUT
    public File put(@PathParam("projectId") Integer projectId, @Valid File file) {

        Optional<File> optional = fileService.updateFile(projectId, file.getFilePath(), file);

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new WebApplicationException("resource not updated.", HttpStatus.SC_INTERNAL_SERVER_ERROR);

    }

    @GET
    @Path("/{filePath}")
    public File get(@PathParam("projectId") Integer projectId, @PathParam("filePath") String filePath) {

        Optional<File> optional = fileService.getFile(projectId, filePath);

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new WebApplicationException("no resource found.", HttpStatus.SC_NOT_FOUND);

    }

    @DELETE
    @Path("/{filePath}")
    public Response delete(@PathParam("projectId") Integer projectId, @PathParam("filePath") String filePath) {

        try {
            fileService.deleteFile(projectId, filePath);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.noContent().build();

    }

}
