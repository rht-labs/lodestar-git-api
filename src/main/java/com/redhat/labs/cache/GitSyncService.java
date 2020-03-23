package com.redhat.labs.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.cache.cacheStore.ResidencyDataCache;
import com.redhat.labs.omp.models.filesmanagement.SingleFileResponse;

import io.quarkus.vertx.ConsumeEvent;

@ApplicationScoped
public class GitSyncService {
    public static Logger LOGGER = LoggerFactory.getLogger(GitSyncService.class);
    
    public static final String FILE_CACHE_EVENT = "fileCacheEvent";
    
    @Inject
    ResidencyDataCache cache;
    
    @ConsumeEvent(FILE_CACHE_EVENT)
    public void consumeTemplate(SingleFileResponse message) {
        LOGGER.warn("Caching repo file event::::  {} ", message.cacheKey);
        cache.store(message);
    }
}
