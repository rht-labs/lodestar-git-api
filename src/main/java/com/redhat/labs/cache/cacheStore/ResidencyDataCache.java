package com.redhat.labs.cache.cacheStore;

import com.redhat.labs.cache.ResidencyDataStore;
import com.redhat.labs.cache.ResidencyInformation;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A very simple facade to write the cache data to remote JDG caches.
 *
 * @author faisalmasood, Donal Spring & Fred Permantier ❤️
 */
@Singleton
public class ResidencyDataCache implements ResidencyDataStore {

    public static Logger logger = LoggerFactory.getLogger(ResidencyDataCache.class);

    @ConfigProperty(name = "cacheServerName", defaultValue = "127.0.0.1")
    public String cacheServerName;

    @PostConstruct
    public void init() {
        org.infinispan.client.hotrod.configuration.ConfigurationBuilder cb
                = new org.infinispan.client.hotrod.configuration.ConfigurationBuilder();

        try {
            cb.marshaller(new org.infinispan.commons.marshall.JavaSerializationMarshaller())
                    .addJavaSerialWhiteList("com.redhat.labs.cache.*")
                    .statistics()
                    .enable()
                    .jmxDomain("org.infinispan")
                    .addServer()
                    .host(cacheServerName)
                    .port(11222)
                    .security()
                    .authentication()
                    .username("omp")
                    .password("omp");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.remoteCacheManager = new RemoteCacheManager(cb.build());
        logger.info("Trying to get the cache");
        this.cache = remoteCacheManager.getCache();

    }

    //    @Inject @Remote("myCache")
    private RemoteCache<String, Object> cache;

    public RemoteCacheManager getRemoteCacheManager() {
        return remoteCacheManager;
    }

    private RemoteCacheManager remoteCacheManager;


    @Override
    public void store(String key, ResidencyInformation residencyInformation) {
        cache.put(key, residencyInformation);
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
}
