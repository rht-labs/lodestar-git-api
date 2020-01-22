package com.redhat.labs.omp.resources;

import com.redhat.labs.omp.models.GetFileResponse;
import com.redhat.labs.omp.models.filesmanagement.SingleFileResponse;
import com.redhat.labs.omp.resources.filters.Logged;
import com.redhat.labs.omp.services.GitLabService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Path("/api/file")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FileResource {
    public static Logger logger = LoggerFactory.getLogger(FileResource.class);

    @Inject
    @RestClient
    GitLabService gitLabService;

    @GET
    @Logged
    public SingleFileResponse getFileFromGitByName(@QueryParam("name") String fileName, @QueryParam("repo_id") String repoId, @QueryParam("branch") String branch) {
        return this.fetchContentFromGit(fileName, repoId, branch);
    }


    public SingleFileResponse fetchContentFromGit(String fileName, String templateRepositoryId, String branch) {
        GetFileResponse metaFileResponse = gitLabService.getFile(templateRepositoryId, fileName, branch == null ? "master" : branch);
        String base64Content = metaFileResponse.content;
        String content = new String(Base64.getDecoder().decode(base64Content), StandardCharsets.UTF_8);
        logger.info("File {} content fetched {}", fileName, content);
        return new SingleFileResponse(fileName, content);
    }
    public SingleFileResponse fetchContentFromGit(String fileName, String templateRepositoryId) {
        return this.fetchContentFromGit(fileName, templateRepositoryId, null);
    }
}