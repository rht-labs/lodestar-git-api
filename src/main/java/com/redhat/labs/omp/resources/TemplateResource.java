package com.redhat.labs.omp.resources;

import com.redhat.labs.cache.GitSyncService;
import com.redhat.labs.omp.models.gitlab.request.CommitMultipleFilesInRepsitoryRequest;
import com.redhat.labs.omp.models.gitlab.response.GetMultipleFilesResponse;
import com.redhat.labs.omp.models.gitlab.response.RepositoryFile;
import com.redhat.labs.omp.resources.filters.Logged;
import com.redhat.labs.omp.rest.client.GitLabService;

import io.vertx.axle.core.eventbus.EventBus;

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
import java.util.ArrayList;
import java.util.List;

@Path("/api/templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemplateResource {
    public static Logger LOGGER = LoggerFactory.getLogger(TemplateResource.class);

    @Inject
    @RestClient
    public GitLabService gitLabService;

    @ConfigProperty(name = "templateRepositoryId", defaultValue = "9407")
    protected String templateRepositoryId;

    @ConfigProperty(name = "metaFileFolder", defaultValue = "schema")
    protected String metaFileFolder;

    @Inject
    protected FileResourceOld fileResource;

    @Inject
    protected EventBus bus;

    /**
     * This method returns a map which contains filename to filecontent rows
     *
     * @return
     */
    @GET
    @Logged
    public GetMultipleFilesResponse getAllFilesFromGit() {

        List<RepositoryFile> allFiles = new ArrayList<>(10);

        //TODO cache this
        RepositoryFile metaFileContent = fileResource.fetchContentFromGit(metaFileFolder + "/meta.dat", templateRepositoryId);

        bus.publish(GitSyncService.FILE_CACHE_EVENT, metaFileContent);

        String[] lines = metaFileContent.getFileContent().split("\\r?\\n");
        for (String line : lines) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("line " + " : " + metaFileFolder + line.substring(1));
            }
            RepositoryFile fileResponse = fileResource.fetchContentFromGit(metaFileFolder + line.substring(1), templateRepositoryId);
            allFiles.add(fileResponse);
        }

        GetMultipleFilesResponse getMultipleFilesResponse = new GetMultipleFilesResponse();
        getMultipleFilesResponse.files = allFiles;
        return getMultipleFilesResponse;

    }


    public void commitMultipleFilesToRepository(Integer repositoryId, CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequest) {
        assert (repositoryId != null);
        assert (commitMultipleFilesInRepsitoryRequest.branch != null);
        assert (commitMultipleFilesInRepsitoryRequest.commitMessage != null);
        LOGGER.info("Trying to commit upload files {} into repository {}", commitMultipleFilesInRepsitoryRequest, repositoryId);
        gitLabService.createFilesInRepository(repositoryId, commitMultipleFilesInRepsitoryRequest);

    }

}
