package com.redhat.labs.omp.resources;

import com.redhat.labs.cache.cacheStore.ResidencyDataCache;
import com.redhat.labs.omp.models.GetFileResponse;
import com.redhat.labs.omp.models.filesmanagement.SingleFileResponse;
import com.redhat.labs.omp.resources.filters.Logged;
import com.redhat.labs.omp.services.GitLabService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Path("/api/cache")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CacheResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheResource.class);

    @ConfigProperty(name = "templateRepositoryId", defaultValue = "9407")
    protected String templateRepositoryId;

    @ConfigProperty(name = "configFileFolder", defaultValue = "schema")
    protected String configFileFolder;

    @Inject
    @RestClient
    protected GitLabService gitLabService;

    public CacheResource() {
        residencyDataCacheForConfig = new ResidencyDataCache();
    }

    private final ResidencyDataCache residencyDataCacheForConfig;

    private static final String CONFIG_FILE_CACHE_KEY = "configFile";

    /**
     * This will trigger a fetch from Git/config.yaml and store it in cache
     *
     * @return
     */
    @POST
    @Logged
    public Response updateConfigFromCache() {
        SingleFileResponse configFileContent = fetchContentFromGit(configFileFolder + "/config.yaml");
        residencyDataCacheForConfig.store(CONFIG_FILE_CACHE_KEY, configFileContent.getFileContent());
        return Response.ok().build();
    }

    private SingleFileResponse fetchContentFromGit(String fileName) {
        GetFileResponse metaFileResponse = gitLabService.getFile(templateRepositoryId, fileName, "master");
        String base64Content = metaFileResponse.content;
        String content = new String(Base64.getDecoder().decode(base64Content), StandardCharsets.UTF_8);
        LOGGER.info("File {} content fetched {}", fileName, content);
        return new SingleFileResponse(fileName, content);
    }
}
