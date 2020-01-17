package com.redhat.labs.cache;

import com.redhat.labs.cache.cacheStore.DataCache;
import io.quarkus.test.junit.QuarkusTest;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.infinispan.server.hotrod.test.HotRodTestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.infinispan.test.fwk.TestResourceTracker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
class DataCacheTest {

//    @RegisterExtension
//    static InfinispanServerExtension server = new InfinispanServerExtension();

    @BeforeAll
    public static void init(){
//        Map<String, String> a = server.start();
//
//        RemoteCacheManager rcm = server.hotRodClient();
////        rcm.start();
//
//
////        System.out.println("******************\n\n\n" + rcm.getCacheNames() + "\n\n\n\n************");
//        rcm.administration().createCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME, "org.infinispan.DIST_SYNC");
////        rcm.administration().createCache("myCache", (BasicConfiguration) null);

        TestResourceTracker.setThreadTestName("InfinispanServer");
        EmbeddedCacheManager ecm = TestCacheManagerFactory.createCacheManager(
                new GlobalConfigurationBuilder().nonClusteredDefault().defaultCacheName("default"),
                new ConfigurationBuilder());
        ecm.createCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME, new ConfigurationBuilder().indexing().build());
        ecm.createCache("myCache", new ConfigurationBuilder().indexing().build());
        // Client connects to a non default port
        HotRodTestingUtil.startHotRodServer(ecm, 11222);
    }


    @Inject
    DataCache dataCache;

    @Test
    public void testPut(){


        dataCache.put("a", "b");
    }

}