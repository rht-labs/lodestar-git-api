package com.redhat.labs.omp.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.redhat.labs.omp.models.*;
import com.redhat.labs.omp.services.GitLabService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.spi.NotImplementedYetException;
import javax.ws.rs.core.Response;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
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
        try {
            GitLabCreateFileInRepositoryRequest gitLabRequest = new GitLabCreateFileInRepositoryRequest(
                    request.filePath, request.branch, request.comment, convert(request));
            return gitLabService.createFileInRepository(request.projectId, request.filePath, gitLabRequest).getEntity();
        } catch (NotImplementedYetException e) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("{project_id}")
    public Object deleteProject(@PathParam("project_id") String projectId) {
        return gitLabService.deleteProject(projectId).getEntity();
    }

    @POST
    public GitLabCreateProjectResponse createNewProject(CreateProjectRequest request) {
        GitLabCreateProjectRequest gitLabRequest = new GitLabCreateProjectRequest();
        gitLabRequest.name = request.residencyName;

        gitLabRequest.namespace_id = 3060;
        //3060
        return gitLabService.createNewProject(gitLabRequest);
    }

    private static byte[] convert(CreateFileRequest request) throws IOException {
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
                return ((String) request.content).getBytes(StandardCharsets.UTF_8);
            } else {
                throw new NotImplementedYetException("Unsupported content format");
            }
        }

        return objectMapper.writeValueAsBytes(request.content);
    }

}