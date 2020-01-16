package com.rht_labs.omp.resources;

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
        GitLabCreateFileInRepositoryRequest gitLabRequest = new GitLabCreateFileInRepositoryRequest(request.filePath, request.branch, request.comment, request.content);
        return gitLabService.createFileInRepository(request.projectId, request.filePath, gitLabRequest);
    }

    @POST
    public Object createNewProject(CreateProjectRequest request) {
        GitLabCreateProjectRequest gitLabRequest = new GitLabCreateProjectRequest();
        gitLabRequest.name = request.residencyName;
        return gitLabService.createNewProject(gitLabRequest);

//        // 1. Create YAML from request obj
//        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//        try {
//            String yaml = mapper.writeValueAsString(request);
//
//            // 2. Add base64 encoded yaml to content key
//            EditFileInGitRequest configurationToWriteToGitLabAndSomeOtherStuff = new EditFileInGitRequest();
//            configurationToWriteToGitLabAndSomeOtherStuff.content = Base64.getEncoder().encodeToString(yaml.getBytes());
//            configurationToWriteToGitLabAndSomeOtherStuff.file_path = request.fileName;
//
//            // 3. send req to gitlab with new id of project and set filename (url encoded)
//            // String fileName = URLEncoder.encode(body.fileName, StandardCharsets.UTF_8.toString());
//            return gitLabService.editFileInRepo(gitlabResponse.id, request.fileName,
//                                    configurationToWriteToGitLabAndSomeOtherStuff).getEntity();
//
//        } catch (Throwable err) {
//            // todo something with err #YOLO
//            err.printStackTrace();
//            return null;
//        }
        
    }
}