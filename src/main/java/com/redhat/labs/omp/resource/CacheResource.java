package com.redhat.labs.omp.resource;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.redhat.labs.cache.EngagementInformation;
import com.redhat.labs.cache.cacheStore.EngagementDataCache;
import com.redhat.labs.exception.FileNotFoundException;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.resources.filter.Logged;
import com.redhat.labs.omp.service.FileService;

@Path("/api/cache")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CacheResource {

    @ConfigProperty(name = "templateRepositoryId", defaultValue = "9407")
    protected Integer templateRepositoryId;

    @ConfigProperty(name = "configFileFolder", defaultValue = "schema")
    protected String configFileFolder;

    @Inject
    FileService fileService;

    @Inject
    protected EngagementDataCache cache;

    public CacheResource() {
        residencyDataCacheForConfig = new EngagementDataCache();
    }

    private final EngagementDataCache residencyDataCacheForConfig;

    private static final String CONFIG_FILE_CACHE_KEY = "configFile";

    /**
     * This will trigger a fetch from Git/config.yaml and store it in cache
     *
     * @return
     */
    @POST
    @Logged
    public Response updateConfigFromCache() {
        File configFileContent = fetchContentFromGit(configFileFolder + "/config.yaml");
        residencyDataCacheForConfig.store(CONFIG_FILE_CACHE_KEY, configFileContent.getContent());
        return Response.ok().build();
    }

    @GET
    @Logged
    @Path("clean-cache")
    public Response cleanCache() {
        cache.cleanCache();
        return Response.ok().build();
    }

    // TODO this method here showing ResInfo is not set up for caching
    @GET
    public Response testResCache() {
        EngagementInformation ri = new EngagementInformation("yaml", new Object());
        cache.store("banana2", ri);
        return Response.ok().build();
    }

    private File fetchContentFromGit(String fileName) {

        Optional<File> optional = fileService.getFile(templateRepositoryId, fileName);
        if (!optional.isPresent()) {
            throw new FileNotFoundException("file not found in gitlab.");
        }

        // decode
        File file = optional.get();
        file.decodeFileAttributes();
        return file;

    }
}
