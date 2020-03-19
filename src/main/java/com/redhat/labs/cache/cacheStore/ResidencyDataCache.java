package com.redhat.labs.cache.cacheStore;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.cache.ResidencyDataStore;
import com.redhat.labs.cache.ResidencyInformation;
import com.redhat.labs.omp.models.filesmanagement.SingleFileResponse;

import io.quarkus.infinispan.client.Remote;

/**
 * A very simple facade to write the cache data to remote JDG caches.
 *
 * @author faisalmasood, Donal Spring & Fred Permantier ❤️
 */
@ApplicationScoped
public class ResidencyDataCache implements ResidencyDataStore {

    public static Logger LOGGER = LoggerFactory.getLogger(ResidencyDataCache.class);

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
    public void store(String key, SingleFileResponse file) {
    	cache.put(key,  file);
    }

    public void store(String key, String file) {
        cache.put(key, file);
    }

    @Override
    public ResidencyInformation fetch(String key) {
        return (ResidencyInformation) cache.get(key);
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
}
