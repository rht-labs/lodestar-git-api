package com.redhat.labs.omp.mocks;

import com.redhat.labs.omp.models.GetFileResponse;
import com.redhat.labs.omp.models.GitLabCreateFileInRepositoryRequest;
import com.redhat.labs.omp.models.GitLabCreateProjectRequest;
import com.redhat.labs.omp.models.GitLabCreateProjectResponse;
import com.redhat.labs.omp.models.filesmanagement.CommitMultipleFilesInRepsitoryRequest;
import com.redhat.labs.omp.services.GitLabService;
import com.redhat.labs.utils.ResourceLoader;
import io.quarkus.test.Mock;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Mock
@ApplicationScoped
@RestClient
public class MockGitLabService implements GitLabService {
    @Override
    public Response getProjects() {
        return Response.status(Response.Status.OK).entity(ResourceLoader.load("getProjects-001-response.json")).build();
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
    public Response createFileInRepository(String projectId, String filePath, GitLabCreateFileInRepositoryRequest request) {
        return Response.status(Response.Status.OK).entity(ResourceLoader.load("createFile-001-response.json")).build();
    }

    @Override
    public Response createFilesInRepository(Integer projectId, CommitMultipleFilesInRepsitoryRequest request) {
        return null;
    }

    @Override
    public GetFileResponse getFile(String projectId, String filePath, String ref) {
        GetFileResponse gfr = new GetFileResponse();
        if (filePath.endsWith("meta.dat")) {
            gfr.fileName = filePath;
            gfr.content = new String(Base64.getEncoder()
                    .encode(ResourceLoader.load("meta.dat").getBytes(StandardCharsets.UTF_8)));
        } else {
            gfr.fileName = filePath;
            gfr.content = new String(Base64.getEncoder()
                    .encode(ResourceLoader.load("residency.yml").getBytes(StandardCharsets.UTF_8)));
        }
        return gfr;
    }
}
