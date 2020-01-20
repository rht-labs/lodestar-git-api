package com.redhat.labs.omp.resources;

import com.redhat.labs.omp.models.CreateFileRequest;
import com.redhat.labs.omp.models.CreateGroupRequest;
import com.redhat.labs.omp.models.CreateGroupResponse;
import com.redhat.labs.omp.models.GitLabCreateFileInRepositoryRequest;
import com.redhat.labs.omp.services.GitLabService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.spi.NotImplementedYetException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/api/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupsResource {
    @Inject
    @RestClient
    protected GitLabService gitLabService;

    @POST
    public CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest){
        assert (createGroupRequest.name != null);
        return  gitLabService.createGroup(createGroupRequest);
    }

}
