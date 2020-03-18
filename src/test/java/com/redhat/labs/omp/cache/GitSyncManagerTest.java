package com.redhat.labs.omp.cache;

import com.redhat.labs.cache.GitSyncManager;
import com.redhat.labs.cache.eventHandlers.CreateProjectEventHandler;
import com.redhat.labs.omp.mocks.MockHotRodServer;

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
        hs =  MockHotRodServer.getHotRod();
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










