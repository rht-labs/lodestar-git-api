package com.redhat.labs.omp.cache;

import org.junit.jupiter.api.extension.ExtendWith;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.junit5.VertxExtension;


@ExtendWith(VertxExtension.class)
@QuarkusTest
class GitSyncManagerTest {

//    @Inject
//    Vertx vertx;
//
//    @Inject
//    GitSyncManager gitSyncManager;
//
//    private static HotRodServer hs;
//
//    @BeforeAll
//    public static void init() {
//        hs =  MockHotRodServer.getHotRod();
//    }
//
//    @AfterAll
//    public static void teardown() {
//        if (hs != null) {
//            hs.stop();
//        }
//    }
//
//    @Test
//    public void testEventReceivedForCreateProject(VertxTestContext vertxTestContext) throws Throwable {
//        String channelName = (new CreateProjectEventHandler()).getChannelName();
//        CreateProjectEventHandler createProjectEventHandlerMock = Mockito.mock(CreateProjectEventHandler.class);
//
//
//        gitSyncManager.addEventHandler( channelName, createProjectEventHandlerMock);
//
//        vertx.eventBus().publish(channelName, new JsonObject().put("a", "b"));
//
//
//        verify(createProjectEventHandlerMock, timeout(2000).times(1)).handleEvent(any());
//        vertxTestContext.completeNow();
//
//
//    }
}










