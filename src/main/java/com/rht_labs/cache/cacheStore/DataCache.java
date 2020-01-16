package com.rht_labs.cache.cacheStore;

import io.quarkus.infinispan.client.Remote;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A very simple facade to write the cache data to remote JDG caches.
 *
 * @author faisalmasood
 */
@Singleton
public class DataCache {

    @Inject
    DataCache(RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
    }

    @Inject @Remote("myCache")
    RemoteCache<String, String> cache;

    RemoteCacheManager remoteCacheManager;

    public void put(String key, String value){
        cache.put(key, value);
    }

    public Object get(String key){
        return cache.get(key);
    }
}
