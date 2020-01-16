package com.rht_labs.omp.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rht_labs.omp.models.CreateFileRequest;
import com.rht_labs.omp.models.CreateProjectRequest;
import com.rht_labs.omp.models.GitLabCreateFileInRepositoryRequest;
import com.rht_labs.omp.models.GitLabCreateProjectRequest;
import com.rht_labs.omp.services.GitLabService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectsResource {
    @Inject
    @RestClient
    protected GitLabService gitLabService;

    @GET
    public Object listAllProjects() {
        return gitLabService.getProjects().getEntity();
    }

    @PUT
    public Object createFileInRepository(CreateFileRequest request) {
        GitLabCreateFileInRepositoryRequest gitLabRequest = new GitLabCreateFileInRepositoryRequest(request.filePath, request.branch, request.comment, convertJson(request));
        return gitLabService.createFileInRepository(request.projectId, request.filePath, gitLabRequest);
    }

    @POST
    public Object createNewProject(CreateProjectRequest request) {
        GitLabCreateProjectRequest gitLabRequest = new GitLabCreateProjectRequest();
        gitLabRequest.name = request.residencyName;
        return gitLabService.createNewProject(gitLabRequest);
    }

    private static byte[] convertJson(CreateFileRequest request) {
        try {
            ObjectMapper objectMapper;

            switch (request.convertTo) {
                case YAML:
                    objectMapper = new ObjectMapper(new YAMLFactory());
                    break;
                default:
                    objectMapper = new ObjectMapper();
            }

            return objectMapper.writeValueAsBytes(request.content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}