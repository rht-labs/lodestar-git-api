package com.redhat.labs.cache.eventHandlers;

import com.redhat.labs.cache.ResidencyDataStore;
import com.redhat.labs.cache.EventHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * an {@link EventHandler} for get a list of projects from the cache
 * @author faisalmasood
 */

public class GetAllProjectEventHandler implements EventHandler {
    public static Logger logger = LoggerFactory.getLogger(GetAllProjectEventHandler.class);

    @Override
    public   void handleEvent(Message<JsonObject> getAllProjectsMessage) {
        logger.info("Get All Project Event Received {}", getAllProjectsMessage);
        JsonObject getAllProjectsRequest = getAllProjectsMessage.body();
        logger.info("{} object received to create a project", getAllProjectsRequest);


        getAllProjectsMessage.reply("Success");
    }

    @Override
    public String getChannelName() {
        return CHANNEL_GET_ALL_PROJECT_EVENTS;
    }

    @Override
    public void setPersistenceStore(ResidencyDataStore residencyDataStore) {
        this.residencyDataStore = residencyDataStore;
    }
    private ResidencyDataStore residencyDataStore;

    private static final String CHANNEL_GET_ALL_PROJECT_EVENTS = "cache.getAllProjects";

}
