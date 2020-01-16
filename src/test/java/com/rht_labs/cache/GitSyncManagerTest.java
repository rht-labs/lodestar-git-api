package com.rht_labs.cache;

import com.rht_labs.cache.eventHandlers.CreateProjectEventHandler;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
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










