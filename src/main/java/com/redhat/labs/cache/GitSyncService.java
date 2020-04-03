package com.redhat.labs.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.cache.cacheStore.EngagementDataCache;
import com.redhat.labs.omp.models.gitlab.File;

import io.quarkus.vertx.ConsumeEvent;

@ApplicationScoped
public class GitSyncService {
    public static Logger LOGGER = LoggerFactory.getLogger(GitSyncService.class);
    
    public static final String FILE_CACHE_EVENT = "fileCacheEvent";
    
    @Inject
    EngagementDataCache cache;
    
    @ConsumeEvent(FILE_CACHE_EVENT)
    public void consumeTemplate(File message) {
        LOGGER.warn("Caching repo file event::::  {} ", message.getCacheKey());
        cache.store(message);
    }
}
