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
import com.redhat.labs.omp.models.gitlab.Group;
import com.redhat.labs.omp.service.GroupService;

@Path("/api/v1/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource {

    @Inject
    GroupService groupService;

    @POST
    public Response post(Group group) {

        Optional<Group> optional = groupService.createGitLabGroup(group);

        if (optional.isPresent()) {
            return Response.status(HttpStatus.SC_CREATED).entity(optional.get()).build();
        }

        return Response.serverError().build();

    }

    @GET
    @Path("/names/{name}")
    public Group get(@PathParam("name") String name) {

        Optional<Group> optional;
        try {
            optional = groupService.getGitLabGroupByName(name);
        } catch (UnexpectedGitLabResponseException e) {
            optional = Optional.empty();
        }
        if (optional.isPresent()) {
            return optional.get();
        }

        throw new WebApplicationException("No resource found with name.", HttpStatus.SC_NOT_FOUND);

    }

    @PUT
    @Path("/{id}")
    public Group put(@PathParam("id") Integer groupId, Group group) {

        Optional<Group> optional = groupService.updateGitLabGroup(groupId, group);

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new WebApplicationException("resource not updated.", HttpStatus.SC_INTERNAL_SERVER_ERROR);

    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer groupId) {

        try {
            groupService.deleteGroup(groupId);
        } catch (Exception e) {
           return  Response.serverError().build();
        }

        return Response.noContent().build();

    }
}
