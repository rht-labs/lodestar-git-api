package com.redhat.labs.cache;

import com.redhat.labs.cache.eventHandlers.CreateProjectEventHandler;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfigurationBuilder;
import org.infinispan.server.hotrod.test.HotRodTestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.infinispan.test.fwk.TestResourceTracker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(VertxExtension.class)
@QuarkusTest
class GitSyncManagerTest {

    @Inject
    Vertx vertx;

    @Inject
    GitSyncManager gitSyncManager;

    private static HotRodServer hs;

    @BeforeAll
    public static void init() {
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
//        GlobalConfigurationBuilder builder = new GlobalConfigurationBuilder();
//
//
//        builder.marshaller(JavaSerializationMarshaller.class)
//                .addJavaSerialWhiteList("org.infinispan.example.*", "org.infinispan.concrete.SomeClass");


        EmbeddedCacheManager ecm = TestCacheManagerFactory.createCacheManager(
                new GlobalConfigurationBuilder().nonClusteredDefault().defaultCacheName("default"),
                new ConfigurationBuilder());
        ecm.createCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME, new ConfigurationBuilder().indexing().build());
        ecm.createCache("myCache", new ConfigurationBuilder().indexing().build());
        // Client connects to a non default port
        HotRodServerConfigurationBuilder hcb = new HotRodServerConfigurationBuilder();

        hs =  HotRodTestingUtil.startHotRodServer(ecm, 11222);
//        hs.setMarshaller(new org.infinispan.commons.marshall.JavaSerializationMarshaller());


    }

    @AfterAll
    public static void teardown() {
        if (hs != null) {
            hs.stop();
        }
    }

    @Test
    public void testEventReceivedForCreateProject(VertxTestContext vertxTestContext) throws Throwable {
        String channelName = (new CreateProjectEventHandler()).getChannelName();
        CreateProjectEventHandler createProjectEventHandlerMock = Mockito.mock(CreateProjectEventHandler.class);


        gitSyncManager.addEventHandler( channelName, createProjectEventHandlerMock);

        vertx.eventBus().publish(channelName, new JsonObject().put("a", "b"));


        verify(createProjectEventHandlerMock, timeout(2000).times(1)).handleEvent(any());
        vertxTestContext.completeNow();


    }
}










