package com.redhat.labs.omp.mocks;

import com.redhat.labs.cache.ResidencyInformation;
import com.redhat.labs.cache.cacheStore.ResidencyDataCache;
import io.quarkus.test.Mock;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mock
@Singleton
public class MockResidencyDataCache extends ResidencyDataCache {
    Map<String, Object> cache = new HashMap<String, Object>();


    @PostConstruct
    public void init() {
    }

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
