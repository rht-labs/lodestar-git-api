package com.redhat.labs.lodestar.rest.client;

import java.util.List;
import java.util.Optional;

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
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import com.redhat.labs.lodestar.exception.mapper.GitLabServiceResponseMapper;
import com.redhat.labs.lodestar.models.gitlab.CommitMultiple;
import com.redhat.labs.lodestar.models.gitlab.DeployKey;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.models.gitlab.ProjectTransfer;
import com.redhat.labs.lodestar.resources.filter.Logged;

@Path("/api/v4")
@RegisterRestClient(configKey = "gitlab.api")
@RegisterProvider(value = GitLabServiceResponseMapper.class, priority = 50)
@ClientHeaderParam(name = "Private-Token", value = "{com.redhat.labs.lodestar.config.GitLabConfig.getPersonalAccessToken}")
public interface GitLabService {

    // GROUPS

    // reference: https://docs.gitlab.com/ee/api/groups.html#list-a-groups-projects
    @GET
    @Path("/groups/{id}/projects")
    Response getProjectsbyGroup(@PathParam("id") @Encoded Integer groupId, @QueryParam("include_subgroups") @Encoded Boolean includeSubgroups, @QueryParam("per_page") int perPage, @QueryParam("page") int page);

    //reference: https://docs.gitlab.com/ee/api/groups.html#list-a-groups-subgroups
    @GET
    @Path("/groups/{id}/subgroups")
    Response getSubGroups(@PathParam("id") @Encoded Integer groupId, @QueryParam("per_page") Integer perPage, @QueryParam("page") Integer page); 

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
    Response getGroupByName(@QueryParam("search") @Encoded String name, @QueryParam("per_page") int perPage, @QueryParam("page") int page);
    
    @GET
    @Logged
    @Path("/groups/{idOrPath}")
    @Produces("application/json")
    Group getGroupByIdOrPath(@PathParam("idOrPath") @Encoded String idOrPath);

    @DELETE
    @Logged
    @Path("/groups/{id}")
    void deleteGroupById(@PathParam("id") @Encoded Integer groupId);

    // PROJECTS

    // reference: https://docs.gitlab.com/ee/api/projects.html#list-all-projects
    @GET
    @Logged
    @Path("/projects")
    @Produces("application/json")
    Response getProjects();

    @GET
    @Logged
    @Path("/projects")
    @Produces("application/json")
    Response getProjectByName(@QueryParam("search") @Encoded String name, @QueryParam("per_page") int perPage, @QueryParam("page") int page);

    @GET
    @Logged
    @Path("/projects/{id}")
    @Produces("application/json")
    Project getProjectById(@PathParam("id") @Encoded String projectId);

    @POST
    @Logged
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
    
    @POST
    @Logged
    @Path("/projects/{id}/hooks")
    @Produces("application/json")
    Response createProjectHook(@PathParam("id") @Encoded Integer projectId, Hook hook);
    
    @PUT
    @Logged
    @Path("/projects/{id}/hooks/{hookId}")
    @Produces("application/json")
    Response updateProjectHook(@PathParam("id") @Encoded Integer projectId, @PathParam("hookId") @Encoded Integer hookId, Hook hook);
    
    @GET
    @Logged
    @Path("/projects/{id}/hooks")
    @Produces("application/json")
    @Consumes("application/json")
    List<Hook> getProjectHooks(@PathParam("id") @Encoded Integer projectId);

    @PUT
    @Logged
    @Path("/projects/{id}/transfer")
    @Produces("application/json")
    Optional<Project> transferProject(@PathParam("id") @Encoded Integer projectId, ProjectTransfer projectTransfer);

    // FILES

    @GET
    @Logged
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    File getFile(@PathParam("id") @Encoded String projectId, @PathParam("file_path") @Encoded String filePath,
            @QueryParam("ref") @Encoded String ref);

    @GET
    @Logged
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    Response getFileWithResponse(@PathParam("id") @Encoded Integer projectId, @PathParam("file_path") @Encoded String filePath,
            @QueryParam("ref") @Encoded String ref);

    @POST
    @Logged
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    File createFile(@PathParam("id") @Encoded Integer projectId, @PathParam("file_path") @Encoded String filePath,
            File file);

    @PUT
    @Logged
    @Path("/projects/{id}/repository/files/{file_path}")
    @Produces("application/json")
    File updateFile(@PathParam("id") @Encoded Integer projectId, @PathParam("file_path") @Encoded String filePath,
            File file);

    @DELETE
    @Logged
    @Path("/projects/{id}/repository/files/{file_path}")
    void deleteFile(@PathParam("id") @Encoded Integer projectId, @PathParam("file_path") @Encoded String filePath, File file);

    // COMMITS

    @POST
    @Logged
    @Path("/projects/{id}/repository/commits")
    @Produces("application/json")
    Response commitMultipleFiles(@PathParam("id") @Encoded Integer projectId, CommitMultiple commit);
    
    @GET
    @Logged
    @Path("/projects/{id}/repository/commits")
    @Produces("application/json")
    Response getCommitLog(@PathParam("id") @Encoded String projectId, @QueryParam("per_page") int perPage, @QueryParam("page") int page);

    // Deploy Keys

    // reference:
    // https://docs.gitlab.com/ce/api/deploy_keys.html#enable-a-deploy-key
    @POST
    @Logged
    @Path("/projects/{id}/deploy_keys/{deploy_key}/enable")
    @Produces("application/json")
    Response enableDeployKey(@PathParam("id") @Encoded Integer projectId, @PathParam("deploy_key") @Encoded Integer deployKey);
    
    @PUT
    @Logged
    @Path("/projects/{id}/deploy_keys/{deploy_key_id}")
    @Produces("application/json")
    Response updateDeployKey(@PathParam("id") @Encoded Integer projectId, @PathParam("deploy_key_id") @Encoded Integer deployKeyId, DeployKey deployKey);

}