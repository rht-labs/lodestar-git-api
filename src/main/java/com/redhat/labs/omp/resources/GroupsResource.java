package com.redhat.labs.omp.resources;

import com.redhat.labs.omp.models.*;
import com.redhat.labs.omp.services.GitLabService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupsResource {
    //this variable defines the fixed name of the project which is iac (infra as code)
    private static final String RESIDENCY_PROJECT_NAME = "iac";

    @Inject
    @RestClient
    protected GitLabService gitLabService;

    @ConfigProperty(name = "residenciesParentRepositoryId", defaultValue = "6284")
    protected Integer residenciesParentRepositoryId;

    @ConfigProperty(name = "deployKey")
    protected Integer deployKey;

    /**
     * this method creates a group for CUSTOMER_NAME and a sun-group with the PROJECT_NAME and a repo named iac inside it.
     * return the id of the iac project repository
     * <p>
     * logic is as follows
     * - search for group - if none exists create one - this is the customerName
     * - create subgroup with the project name
     * - create project with the name iac inside the subgroup
     * - enjoy
     *
     * @return
     */
    @POST
    public GitLabCreateProjectResponse createResidencyStructure(CreateResidencyGroupStructure createResidencyGroupStructure) {
        // Search or Create Group
        Integer groupId = getOrCreateGroup(createResidencyGroupStructure.customerName);
        if (groupId == null) throw new RuntimeException("Unable to search/create customer name group in gitlab");

        //Create subgroup
        CreateGroupRequest createSubGroupRequest = new CreateGroupRequest();
        createSubGroupRequest.name = createResidencyGroupStructure.projectName;
        createSubGroupRequest.path = createResidencyGroupStructure.projectName;
        createSubGroupRequest.parent_id = groupId;
        CreateGroupResponse createSubGroupResponse = gitLabService.createGroup(createSubGroupRequest);
        if (createSubGroupResponse.id == null)
            throw new RuntimeException("Unable to search/create project name group in gitlab");

        // Create Project
        GitLabCreateProjectRequest gitLabCreateProjectRequest = new GitLabCreateProjectRequest();
        gitLabCreateProjectRequest.namespace_id = createSubGroupResponse.id;
        gitLabCreateProjectRequest.name = RESIDENCY_PROJECT_NAME;
        GitLabCreateProjectResponse gitLabProject = gitLabService.createNewProject(gitLabCreateProjectRequest);
        gitLabService.enableDeployKey(gitLabProject.id, deployKey);
        return gitLabProject;

        //first try to fetch the customer_name group and get the eid, if doesn't exists create a group.
        //then create the project . if project exists freak out
        //create iac repo.
    }

    private Integer getOrCreateGroup(String groupName) {
        assert groupName != null;
        SearchGroupResponse[] searchGroupResponse = gitLabService.searchGroup(groupName);
        if (searchGroupResponse != null && searchGroupResponse.length > 1)
            throw new RuntimeException("Too many projects found for the requested name. Expected only one");

        if (searchGroupResponse == null || searchGroupResponse.length == 0) {

            CreateGroupRequest createGroupRequest = new CreateGroupRequest();
            createGroupRequest.name = groupName;
            createGroupRequest.path = groupName;
            createGroupRequest.parent_id = residenciesParentRepositoryId;

            CreateGroupResponse createGroupResponse = gitLabService.createGroup(createGroupRequest);

            return createGroupResponse.id;
        }
        return searchGroupResponse[0].id;
    }
}
