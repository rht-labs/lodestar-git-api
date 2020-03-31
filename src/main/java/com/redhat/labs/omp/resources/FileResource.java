package com.redhat.labs.omp.resources;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.cache.GitSyncService;
import com.redhat.labs.cache.cacheStore.ResidencyDataCache;
import com.redhat.labs.omp.models.gitlab.response.GetFileResponse;
import com.redhat.labs.omp.models.gitlab.response.RepositoryFile;
import com.redhat.labs.omp.resources.filters.Logged;
import com.redhat.labs.omp.rest.client.GitLabService;

import io.vertx.axle.core.eventbus.EventBus;

@Path("/api/file")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FileResource {
    public static final Logger LOGGER = LoggerFactory.getLogger(FileResource.class);

    @Inject
    @RestClient
    protected GitLabService gitLabService;
    
    @Inject
    protected EventBus bus;

    @Inject
    protected ResidencyDataCache cache;

    @ConfigProperty(name = "file_branch", defaultValue = "master")
    protected String defaultBranch;

    @GET
    @Logged
    public RepositoryFile getFileFromGitByName(@QueryParam("name") String fileName, @QueryParam("repo_id") String repoId, @QueryParam("branch") String branch) {
        return this.fetchContentFromGit(fileName, repoId, branch);
    }

    public RepositoryFile fetchContentFromGit(String fileName, String templateRepositoryId, String branch) {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Template Repo %s filename %s branch %s", templateRepositoryId, fileName, branch));
        }

        String key = String.format("%s:%s:%s", fileName, templateRepositoryId, branch == null ? defaultBranch : branch);

        String fileContent = cache.fetch(key);
        if(fileContent != null) {
            LOGGER.debug("Cache hit for key {}", key);
            return RepositoryFile.builder().fileName(fileName).fileContent(fileContent).build();
        }

        LOGGER.info("Cache miss for key: {}", key);
        GetFileResponse metaFileResponse = gitLabService.getFile(templateRepositoryId, fileName, branch == null ? defaultBranch : branch);
        String base64Content = metaFileResponse.content;
        String content = new String(Base64.getDecoder().decode(base64Content), StandardCharsets.UTF_8);
        LOGGER.debug("File {} content fetched {}", fileName, content);
        RepositoryFile response = RepositoryFile.builder().fileName(fileName).fileContent(fileContent).build();
        if(content != null) {
            LOGGER.info("adding {} to cache", fileName);
            response.setCacheKey(key);
            bus.publish(GitSyncService.FILE_CACHE_EVENT, response);
        }
        return response;
    }

    public RepositoryFile fetchContentFromGit(String fileName, String templateRepositoryId) {
        return this.fetchContentFromGit(fileName, templateRepositoryId, null);
    }
}