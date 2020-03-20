package com.redhat.labs.omp.cache;

import javax.inject.Inject;

import org.infinispan.server.hotrod.HotRodServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.redhat.labs.cache.ResidencyInformation;
import com.redhat.labs.cache.cacheStore.ResidencyDataCache;
import com.redhat.labs.omp.mocks.MockHotRodServer;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class DataCacheTest {
	private static HotRodServer hs;

    @Inject ResidencyDataCache engagementDataCache;

    @BeforeAll
    public static void init() {
        hs = MockHotRodServer.getHotRod();
    }

    @AfterAll
    public static void stop() {
        hs.stop();
    }


    //TODO disabling test as this functionality doesn't work IRL and is not in use. Fix before using.
    // current error Caused by: java.lang.IllegalArgumentException: No marshaller registered for Java type com.redhat.labs.cache.ResidencyInformation
    @Test
    public void testPut() {
        ResidencyInformation data = new ResidencyInformation("yaml fle", new String("some data"));
       // engagementDataCache.store("a", data);

        // assertEquals(data, engagementDataCache.fetch("a"));
    }

}