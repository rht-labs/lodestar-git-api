package com.redhat.labs.omp.resources;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;

import com.redhat.labs.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.omp.models.gitlab.Project;
import com.redhat.labs.omp.service.ProjectService;

@Path("/api/v1/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    @Inject
    ProjectService projectService;

    @POST
    public Response post(Project project) {

        Optional<Project> optional = projectService.createProject(project);

        if (optional.isPresent()) {
            return Response.status(HttpStatus.SC_CREATED).entity(optional.get()).build();
        }

        return Response.serverError().build();

    }

    @GET
    @Path("/names/{name}")
    public Project getByName(@PathParam("name") String name) {

        Optional<Project> optional;
        try {
            optional = projectService.getProjectByName(name);
        } catch (UnexpectedGitLabResponseException e) {
            optional = Optional.empty();
        }
        if (optional.isPresent()) {
            return optional.get();
        }

        throw new WebApplicationException("no resource found with name", HttpStatus.SC_NOT_FOUND);

    }

    @GET
    @Path("/{id}")
    public Project getById(@PathParam("id") Integer projectId) {

        Optional<Project> optional = projectService.getProjectById(projectId);

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new WebApplicationException("no resource found with name", HttpStatus.SC_NOT_FOUND);

    }

    @PUT
    @Path("/{id}")
    public Project put(@PathParam("id") Integer projectId, Project project) {

        Optional<Project> optional = projectService.updateProject(projectId, project);

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new WebApplicationException("resource not updated.", HttpStatus.SC_INTERNAL_SERVER_ERROR);

    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer projectId) {

        try {
            projectService.deleteProject(projectId);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.noContent().build();

    }

}
