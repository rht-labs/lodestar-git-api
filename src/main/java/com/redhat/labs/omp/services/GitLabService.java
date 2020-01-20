package com.redhat.labs.omp.services;

import com.redhat.labs.omp.models.*;
import com.redhat.labs.omp.models.filesmanagement.CommitMultipleFilesInRepsitoryRequest;
import com.redhat.labs.omp.resources.filters.Logged;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/api/v4")
@RegisterRestClient(configKey = "gitlab.api")
@ClientHeaderParam(name = "Private-Token", value = "{com.redhat.labs.omp.config.GitLabConfig.getPersonalAccessToken}")
public interface GitLabService {
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
    SearchProjectResponse searchProject(@PathParam("search") @Encoded String search);

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
    SearchGroupResponse searchGroup(@PathParam("search") @Encoded String search);


}