package com.redhat.labs.omp.rest.client;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import com.redhat.labs.omp.models.gitlab.Group;
import com.redhat.labs.omp.models.gitlab.Project;
import com.redhat.labs.omp.models.gitlab.request.CommitMultipleFilesInRepsitoryRequest;
import com.redhat.labs.omp.models.gitlab.request.CreateGroupRequest;
import com.redhat.labs.omp.models.gitlab.request.GitLabCreateFileInRepositoryRequest;
import com.redhat.labs.omp.models.gitlab.request.GitLabCreateProjectRequest;
import com.redhat.labs.omp.models.gitlab.response.CreateGroupResponse;
import com.redhat.labs.omp.models.gitlab.response.GetFileResponse;
import com.redhat.labs.omp.models.gitlab.response.GitLabCreateProjectResponse;
import com.redhat.labs.omp.models.gitlab.response.SearchGroupResponse;
import com.redhat.labs.omp.models.gitlab.response.SearchProjectResponse;
import com.redhat.labs.omp.resources.filters.Logged;

@Path("/api/v4")
@RegisterRestClient(configKey = "gitlab.api")
@ClientHeaderParam(name = "Private-Token", value = "{com.redhat.labs.omp.config.GitLabConfig.getPersonalAccessToken}")
public interface GitLabService {

    // PROJECTS

    // reference: https://docs.gitlab.com/ee/api/projects.html#list-all-projects
    @GET
    @Path("/projects")
    @Produces("application/json")
    Response getProjects();

    // reference: https://docs.gitlab.com/ee/api/projects.html#search-for-projects-by-name
    @GET
    @Logged
    @Path("/projects")
    @Produces("application/json")
    SearchProjectResponse searchProject(@QueryParam("search") @Encoded String search);

    // reference: https://docs.gitlab.com/ee/api/projects.html#remove-project
    @DELETE
    @Path("/projects/{id}")
    @Produces("application/json")
    Response deleteProject(@PathParam("id") @Encoded String projectId);

    // reference: https://docs.gitlab.com/ee/api/projects.html#create-project
    @POST
    @Path("/projects")
    @Produces("application/json")
    GitLabCreateProjectResponse createNewProject(GitLabCreateProjectRequest request);

    // Deploy Keys

    // reference: https://docs.gitlab.com/ce/api/deploy_keys.html#enable-a-deploy-key
    @POST
    @Path("/projects/{id}/deploy_keys/{deploy_key}/enable")
    @Produces("application/json")
    Response enableDeployKey(@PathParam("id") @Encoded Integer projectId, @PathParam("deploy_key") @Encoded Integer deployKey);

    // Files

    // reference: https://docs.gitlab.com/ee/api/repository_files.html#create-new-file-in-repository
    @POST
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    Response createFileInRepository(@PathParam("id") @Encoded String projectId, @PathParam("file_path") @Encoded String filePath, GitLabCreateFileInRepositoryRequest request);

    // reference https://docs.gitlab.com/ee/api/commits.html#create-a-commit-with-multiple-files-and-actions
    @POST
    @Path("/projects/{id}/repository/commits")
    @Produces("application/json")
    Response createFilesInRepository(@PathParam("id") @Encoded Integer projectId, CommitMultipleFilesInRepsitoryRequest request);


    // reference https://docs.gitlab.com/ee/api/repository_files.html
    //https://gitlab.consulting.redhat.com/api/v4/projects/9407/repository/files/schema%2Fmeta.dat?ref=master
    @GET
    @Logged
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    GetFileResponse getFile(@PathParam("id") @Encoded String projectId, @PathParam("file_path") @Encoded String filePath, @QueryParam("ref") @Encoded String ref);

    // Groups - CRUD

    // reference: https://docs.gitlab.com/ee/api/groups.html#new-group
    @POST
    @Logged
    @Path("/groups")
    @Produces("application/json")
    @Consumes("application/json")
    CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest);

    // reference: https://docs.gitlab.com/ee/api/groups.html#search-for-group
    @GET
    @Logged
    @Path("/groups")
    @Produces("application/json")
    SearchGroupResponse[] searchGroup(@QueryParam("search") @Encoded String search);

    /*
     * 
     * Using new models
     * 
     * 
     */

    // reference: https://docs.gitlab.com/ee/api/groups.html#new-group
    @POST
    @Logged
    @Path("/groups")
    @Produces("application/json")
    @Consumes("application/json")
    Group createGroup(Group group);

    @PUT
    @Logged
    @Path("/groups/{id}")
    @Produces("application/json")
    @Consumes("application/json")
    Group updateGroup(@PathParam("id") @Encoded Integer groupId, Group group);

    @GET
    @Logged
    @Path("/groups")
    @Produces("application/json")
    List<Group> getGroupByName(@QueryParam("search") @Encoded String name);

    @DELETE
    @Logged
    @Path("/groups/{id}")
    void deleteGroupById(@PathParam("id") @Encoded Integer groupId);

    @GET
    @Logged
    @Path("/projects")
    @Produces("application/json")
    List<Project> getProjectByName(@QueryParam("search") @Encoded String name);
 
    @GET
    @Logged
    @Path("/projects/{id}")
    @Produces("application/json")
    Project getProjectById(@PathParam("id") @Encoded Integer projectId);

    @POST
    @Path("/projects")
    @Produces("application/json")
    Project createProject(Project project);

    @PUT
    @Logged
    @Path("/projects/{id}")
    @Produces("application/json")
    @Consumes("application/json")
    Project updateProject(@PathParam("id") @Encoded Integer projectId, Project project);

    @DELETE
    @Logged
    @Path("/projects/{id}")
    void deleteProjectById(@PathParam("id") @Encoded Integer projectId);

}