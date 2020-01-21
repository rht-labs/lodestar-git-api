package com.redhat.labs.omp.resources;

import com.redhat.labs.omp.models.*;
import com.redhat.labs.omp.resources.ProjectsResource;
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

    //

    /**
     * this mentod create a group for CUSTOMER_NAME and a sun-group with the PROJECT_NAME and a repo named iac inside it.
     * return the id of the iac project repositoy
     * @return
     */
    @POST
    public Integer createResidencyStructure(String customerName,  String projectName){
        throw new RuntimeException("Noy implemented");

        if (searchGroupResponse(customerName) != null){
            if (searchProjectResponse(projectName) != null){

            }
        }
        assert (searchGroupResponse(customerName) != null);



        // Search Group

        // Search Project

        // Create Group

        // Get the ID

        // Create Project

        //first try to fetch the custoner_naem group and get tht eid, if doesnot exists create a group.
        //then create the project . if project exists freak out
        //cerate iac repo.


    }

    public CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest){
        assert (createGroupRequest.name != null);
        return  gitLabService.createGroup(createGroupRequest);
    }

    public SearchGroupResponse searchGroupResponse(String search){
        assert (search != null);
        return  gitLabService.searchGroup(search);
    }

}
