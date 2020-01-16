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
import java.nio.charset.StandardCharsets;

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
        GitLabCreateFileInRepositoryRequest gitLabRequest = new GitLabCreateFileInRepositoryRequest(request.filePath, request.branch, request.comment, convert(request));
        return gitLabService.createFileInRepository(request.projectId, request.filePath, gitLabRequest);
    }

    @DELETE
    @Path("{project_id}")
    public Object deleteProject(@PathParam("project_id") String projectId) {
        return gitLabService.deleteProject(projectId);
    }

    @POST
    public Object createNewProject(CreateProjectRequest request) {
        GitLabCreateProjectRequest gitLabRequest = new GitLabCreateProjectRequest();
        gitLabRequest.name = request.residencyName;
        return gitLabService.createNewProject(gitLabRequest);
    }

    private static byte[] convert(CreateFileRequest request) {
        try {
            ObjectMapper objectMapper;

            switch (request.outputFormat) {
                case YAML:
                    objectMapper = new ObjectMapper(new YAMLFactory());
                    break;
                case JSON:
                    objectMapper = new ObjectMapper();
                    break;
                default:
                    if (request.content instanceof String) {
                        return ((String)request.content).getBytes(StandardCharsets.UTF_8);
                    } else {
                        throw new RuntimeException("Unsupported content format");
                    }
            }

            return objectMapper.writeValueAsBytes(request.content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}