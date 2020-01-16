package com.rht_labs.cache.eventHandlers;

import com.rht_labs.cache.EventHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * an {@link EventHandler} for create a new project action.
 *
 * @author faisalmasood
 */

public class CreateProjectEventHandler implements EventHandler {

    public static Logger logger = LoggerFactory.getLogger(CreateProjectEventHandler.class);

    @Override
    public   void handleEvent(Message<JsonObject> createProjectMessage) {
        logger.info("eqweqweqwe");
        if(createProjectMessage == null) return;

        JsonObject createProjectRequest = createProjectMessage.body();
        logger.info("{} object received to create a project", createProjectRequest);
    }

    @Override
    public String getChannelName() {
        return CHANNEL_CREATE_PROJECT_EVENT;
    }




    private static final String CHANNEL_CREATE_PROJECT_EVENT = "gitapi.createProject";
}
