package com.rht_labs.cache;

import com.rht_labs.cache.eventHandlers.CreateProjectEventHandler;
import com.rht_labs.cache.eventHandlers.GetAllProjectEventHandler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class provides a facade for working with cache. We are using the Jboss Data Grid.
 * It listens to the vertx bus in order to communicate with other components.
 * Currently it does following:
 * <p>
 * 1- Listen to createProject Event and store the project info in the cache.
 * 2- Respond to getAllProjects Event from the cached copy
 * 3- Listen to addFile event and store the file
 * 4-
 * <p>
 * Follows a singleton pattern
 *
 * @author faisalmasood
 */
@Singleton
public class GitSyncManager {

    public static Logger logger = LoggerFactory.getLogger(GitSyncManager.class);



    @Inject
    Vertx vertx;

/*    //init all the listeners for the vertx event bus - a utility method
    @PostConstruct
    public void init() {

        logger.info("Adding default consumers....");
//        vertx.eventBus().<JsonObject>consumer(CreateProjectEventHandler.CHANNEL_CREATE_PROJECT_EVENT, new CreateProjectEventHandler()::handleEvent).exceptionHandler(Throwable::printStackTrace);
//        vertx.eventBus().<JsonObject>consumer(GetAllProjectEventHandler.CHANNEL_GET_ALL_PROJECT_EVENTS, new GetAllProjectEventHandler()::handleEvent).exceptionHandler(Throwable::printStackTrace);
//
        //addEventHandler(CreateProjectEventHandler.CHANNEL_CREATE_PROJECT_EVENT, new CreateProjectEventHandler());
    }*/

    public void addEventHandler(String channelName, EventHandler eventHandler) {
        vertx.eventBus().<JsonObject>consumer(channelName, eventHandler::handleEvent).exceptionHandler(Throwable::printStackTrace);

    }



}
