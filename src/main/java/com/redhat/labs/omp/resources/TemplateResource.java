package com.redhat.labs.omp.resources;


import com.redhat.labs.omp.models.GetFileResponse;
import com.redhat.labs.omp.models.filesmanagement.CommitMultipleFilesInRepsitoryRequest;
import com.redhat.labs.omp.models.filesmanagement.GetMultipleFilesResponse;
import com.redhat.labs.omp.models.filesmanagement.SingleFileResponse;
import com.redhat.labs.omp.resources.filters.Logged;
import com.redhat.labs.omp.services.GitLabService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Path("/api/templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemplateResource {
    public static Logger logger = LoggerFactory.getLogger(TemplateResource.class);

    @Inject
    @RestClient
    public GitLabService gitLabService;

    @ConfigProperty(name = "templateRepositoryId", defaultValue = "9407")
    protected String templateRepositoryId;

    @ConfigProperty(name = "metaFileFolder", defaultValue = "schema")
    protected String metaFileFolder;

    @Inject
    FileResource fileResource;

    /**
     * This method returns a map which contains filename to filecontent rows
     *
     * @return
     */
    @GET
    @Logged
    public GetMultipleFilesResponse getAllFilesFromGit() {

        List<SingleFileResponse> allFiles = new ArrayList<>(10);

        SingleFileResponse metaFileContent = fileResource.fetchContentFromGit(metaFileFolder + "/meta.dat", templateRepositoryId);

        String[] lines = metaFileContent.getFileContent().split("\\r?\\n");
        for (String line : lines) {
            String fileName = line;
            logger.info("line " + " : " + metaFileFolder + fileName.substring(1));
            SingleFileResponse fileResponse = fileResource.fetchContentFromGit(metaFileFolder + line.substring(1), templateRepositoryId);
            allFiles.add(fileResponse);
        }

        GetMultipleFilesResponse getMultipleFilesResponse = new GetMultipleFilesResponse();
        getMultipleFilesResponse.files = allFiles; //.toArray(new SingleFileResponse[allFiles.size()]);
        return getMultipleFilesResponse;

    }


    public void commitMultipleFilesToRepository(Integer repositoryId, CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequest) {
        assert (repositoryId != null);
        assert (commitMultipleFilesInRepsitoryRequest.branch != null);
        assert (commitMultipleFilesInRepsitoryRequest.commitMessage != null);
        logger.info("Trying to commit upload files {} into reqoistory {}", commitMultipleFilesInRepsitoryRequest, repositoryId);
        gitLabService.createFilesInRepository(repositoryId, commitMultipleFilesInRepsitoryRequest);

    }

}
