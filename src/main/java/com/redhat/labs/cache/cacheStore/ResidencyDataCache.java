package com.redhat.labs.cache.cacheStore;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.cache.ResidencyDataStore;
import com.redhat.labs.cache.ResidencyInformation;
import com.redhat.labs.omp.models.gitlab.File;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.runtime.StartupEvent;

/**
 * A very simple facade to write the cache data to remote JDG caches.
 *
 * @author faisalmasood, Donal Spring & Fred Permantier ❤️
 */
@ApplicationScoped
public class ResidencyDataCache implements ResidencyDataStore {

    public static Logger LOGGER = LoggerFactory.getLogger(ResidencyDataCache.class);

    void onStart(@Observes StartupEvent ev) {
        LOGGER.debug("On start cache check available ==> {}", cache != null);
        if(cache == null) {
            createCache();
        }
    }

    @Inject
    protected RemoteCacheManager cacheManager;
    
    @Inject @Remote("omp")
    protected RemoteCache<String, Object> cache;

    public RemoteCacheManager getRemoteCacheManager() {
        return cacheManager;
    }

    @Override
    public void store(String key, ResidencyInformation residencyInformation) {
        cache.put(key, residencyInformation);
    }

    @Override
    public void store(File file) {
        cache.put(file.getCacheKey(),  file.getContent());
    }

    public void store(String key, String file) {
        cache.put(key, file);
    }

    @Override
    public String fetch(String key) {
        return (String) cache.get(key);
    }

    @Override
    public List<String> getAllKeys() {
        return StreamSupport
                .stream(cache.keySet().spliterator(), true)
                .collect(Collectors.toList());
    }
    
    public void cleanCache() {
    	cache.clear();
    }

    private void createCache() {
        LOGGER.info("Create OMP cache");
        cache = cacheManager.administration().getOrCreateCache("omp", "default");
    }
}
