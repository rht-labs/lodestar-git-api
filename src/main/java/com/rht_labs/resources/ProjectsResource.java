package com.rht_labs.resources;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rht_labs.models.CreateProject;
import com.rht_labs.models.CreateProjectResponse;
import com.rht_labs.models.EditFileInGit;
import com.rht_labs.services.GitLabService;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectsResource {

    @Inject
    @RestClient
    private GitLabService gitLabService;

    // TODO - add query string to filter by thing eg region, age etc
    @GET
    public Object listAllProjects() {
        return gitLabService.getProjects().getEntity();
    }

    @POST
    public Object createNewProject(CreateProject body) {
        // edit and strip out just the name field

        // 🥰 SEXY code here.... Be in awe of the JavaScript developer writing Java 😂 
        String awesomeRequestObject = "{\"name\":\"" + body.name + "\"}";
        // TODO - what heppens if this errors?
        CreateProjectResponse gitlabResponse = gitLabService.createNewProject(awesomeRequestObject);

        // 1. Create YAML from request obj
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            String yaml = mapper.writeValueAsString(body);

            // 2. Add base64 encoded yaml to content key
            EditFileInGit configurationToWriteToGitLabAndSomeOtherStuff = new EditFileInGit();
            configurationToWriteToGitLabAndSomeOtherStuff.content = Base64.getEncoder().encodeToString(yaml.getBytes());
            configurationToWriteToGitLabAndSomeOtherStuff.file_path = body.fileName;

            // 3. send req to gitlab with new id of project and set filename (url encoded)
            // String fileName = URLEncoder.encode(body.fileName, StandardCharsets.UTF_8.toString());
            return gitLabService.editFileInRepo(gitlabResponse.id, body.fileName,
                                    configurationToWriteToGitLabAndSomeOtherStuff).getEntity();
    
        } catch (Throwable err) {
            // todo something with err #YOLO
            err.printStackTrace();
            return null;
        }
        
    }
}