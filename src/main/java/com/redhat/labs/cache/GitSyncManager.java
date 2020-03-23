package com.redhat.labs.cache;

import com.redhat.labs.cache.cacheStore.ResidencyDataCache;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Deprecated
@Singleton
public class GitSyncManager {

        public static Logger logger = LoggerFactory.getLogger(GitSyncManager.class);



    @Inject
    Vertx vertx;


    @Inject
    ResidencyDataCache dataCache;

    public void addEventHandler(String channelName, EventHandler eventHandler) {
        eventHandler.setPersistenceStore(dataCache);
        vertx.eventBus().<JsonObject>consumer(channelName, eventHandler::handleEvent).exceptionHandler(Throwable::printStackTrace);

    }



}
