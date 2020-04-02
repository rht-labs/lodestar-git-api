package com.redhat.labs.cache.cacheStore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.redhat.labs.cache.cacheStore.EngagementDataCache;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
public class MockEngagementDataCache extends EngagementDataCache {

    Map<String, String> cache = new HashMap<String, String>();

    @PostConstruct
    public void init() {
    }

    void onStart(@Observes StartupEvent ev) {
        // do nothing
    }

    @Override
    public String fetch(String key) {
        assert (key != null);
        return cache.get(key);
    }

    @Override
    public void store(String key, String value) {
        cache.put(key, value);
    }

}
