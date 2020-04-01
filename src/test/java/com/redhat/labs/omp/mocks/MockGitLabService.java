package com.redhat.labs.omp.mocks;

import com.redhat.labs.omp.models.*;
import com.redhat.labs.omp.models.gitlab.CommitMultiple;
import com.redhat.labs.omp.models.gitlab.File;
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
import com.redhat.labs.omp.rest.client.GitLabService;
import com.redhat.labs.utils.ResourceLoader;
import io.quarkus.test.Mock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Encoded;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Mock
@ApplicationScoped
@RestClient
public class MockGitLabService implements GitLabService {
    @Override
    public Response getProjects() {
        return Response.status(Response.Status.OK).entity(ResourceLoader.load("getProjects-001-response.json")).build();
    }

    @Override
    public SearchProjectResponse searchProject(String search) {
        return null;
    }

    @Override
    public Response deleteProject(String projectId) {
        return Response.status(Response.Status.OK).entity(ResourceLoader.load("deleteProject-001-response.json")).build();
    }

    @Override
    public GitLabCreateProjectResponse createNewProject(GitLabCreateProjectRequest request) {
        GitLabCreateProjectResponse response = new GitLabCreateProjectResponse();
        response.id = 1337;
        response.description = "ut description";
        response.name = "ut name";
        return response;
    }

    @Override
    public Response enableDeployKey(Integer projectId, Integer deployKey){
        return Response.status(Response.Status.OK).entity(ResourceLoader.load("enableDeployKey-001-response.json")).build();
    }

    @Override
    public Response createFileInRepository(String projectId, String filePath, GitLabCreateFileInRepositoryRequest request) {
        return Response.status(Response.Status.OK).entity(ResourceLoader.load("createFile-001-response.json")).build();
    }

    @Override
    public Response createFilesInRepository(Integer projectId, CommitMultipleFilesInRepsitoryRequest request) {
        return null;
    }

//    @Override
//    public GetFileResponse getFile(String projectId, String filePath, String ref) {
//        GetFileResponse gfr = new GetFileResponse();
//        if (filePath.endsWith("meta.dat")) {
//            gfr.fileName = filePath;
//            gfr.content = new String(Base64.getEncoder()
//                    .encode(ResourceLoader.load("meta.dat").getBytes(StandardCharsets.UTF_8)));
//        } else {
//            gfr.fileName = filePath;
//            gfr.content = new String(Base64.getEncoder()
//                    .encode(ResourceLoader.load("residency.yml").getBytes(StandardCharsets.UTF_8)));
//        }
//        return gfr;
//    }

    @Override
    public CreateGroupResponse createGroup(CreateGroupRequest createGroupRequest) {
        CreateGroupResponse createGroupResponse = new CreateGroupResponse();
        createGroupResponse.name = createGroupRequest.name;
        createGroupResponse.id = 9;
        createGroupResponse.parent_id = 7;
        return createGroupResponse;
    }

    @Override
    public SearchGroupResponse[] searchGroup(String search) {
        SearchGroupResponse searchGroupResponse = new SearchGroupResponse();
        searchGroupResponse.id = 7;
        searchGroupResponse.name = "customer-name";
        searchGroupResponse.path = "Customer-name";

        return new SearchGroupResponse[]{searchGroupResponse};
    }

    @Override
    public Group createGroup(Group group) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group updateGroup(Integer groupId, Group group) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Group> getGroupByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteGroupById(Integer groupId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Project> getProjectByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Project getProjectById(Integer projectId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Project createProject(Project project) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Project updateProject(Integer projectId, Project project) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteProjectById(Integer projectId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public File getFile(Integer projectId, String filePath, String ref) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public File createFile(Integer projectId, String filePath, File file) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public File updateFile(Integer projectId, String filePath, File file) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteFile(Integer projectId, String filePath, File file) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Response commitMultipleFiles(Integer projectId, CommitMultiple commit) {
        // TODO Auto-generated method stub
        return null;
    }
}
