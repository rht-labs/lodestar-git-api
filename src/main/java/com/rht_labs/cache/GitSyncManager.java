package com.rht_labs.cache;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static com.rht_labs.cache.EventChannels.CHANNEL_CREATE_PROJECT_EVENT;
import static com.rht_labs.cache.EventChannels.CHANNEL_GET_ALL_PROJECT_EVENTS;

/**
 * This class provides a facade for working with cache. We are using the Jboss Data Grid.
 * It listens to the vertx bus in order to communicate with other components.
 * Currently it does following:
 *
 * 1- Listen to createProject Event and store the project info in the cache.
 * 2- Respond to getAllProjects Event from the cached copy
 * 3- Listen to addFile event and store the file
 * 4-
 *
 * @author faisalmasood
 */
public class GitSyncManager {

    public static Logger logger = LoggerFactory.getLogger(GitSyncManager.class);

    @Inject
    Vertx vertx;


    //init all the listeners for the vertx event bus
    @PostConstruct
    public void initListeners(){
        EventBus eventBus = vertx.eventBus();
        eventBus.<JsonObject>consumer(CHANNEL_CREATE_PROJECT_EVENT, GitSyncManager::handleCreateProjectEvent).exceptionHandler(Throwable::printStackTrace);
        eventBus.<JsonObject>consumer(CHANNEL_GET_ALL_PROJECT_EVENTS, GitSyncManager::handleGetAllProjectsEvent).exceptionHandler(Throwable::printStackTrace);;
    }



    private static void handleCreateProjectEvent(Message<JsonObject> createProjectMessage) {
        JsonObject createProjectRequest = createProjectMessage.body();
        logger.info("{} object received to create a project", createProjectRequest);
    }

    private static void handleGetAllProjectsEvent(Message<JsonObject> getAllProjectsMessage) {
        JsonObject getAllProjectsRequest = getAllProjectsMessage.body();
        logger.info("{} object received to create a project", getAllProjectsRequest);
        getAllProjectsMessage.reply("Success");
    }

}
