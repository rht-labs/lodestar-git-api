package com.redhat.labs.omp.mocks;

import com.redhat.labs.omp.models.GetFileResponse;
import com.redhat.labs.omp.models.GitLabCreateFileInRepositoryRequest;
import com.redhat.labs.omp.models.GitLabCreateProjectRequest;
import com.redhat.labs.omp.models.filesmanagement.CreateCommitMultipleFilesRequest;
import com.redhat.labs.omp.services.GitLabService;
import com.redhat.labs.utils.ResourceLoader;
import io.quarkus.test.Mock;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

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
    public Response createNewProject(GitLabCreateProjectRequest request) {
        return Response.status(Response.Status.OK).entity(ResourceLoader.load("createProject-001-response.json")).build();
    }

    @Override
    public Response createFileInRepository(String projectId, String filePath, GitLabCreateFileInRepositoryRequest request) {
        return Response.status(Response.Status.OK).entity(ResourceLoader.load("createFile-001-response.json")).build();
    }

    @Override
    public Response createFilesInRepository(String projectId, CreateCommitMultipleFilesRequest request) {
        return null;
    }

    @Override
    public GetFileResponse getFile(String projectId, String filePath, String ref) {
        return null;
    }
}
