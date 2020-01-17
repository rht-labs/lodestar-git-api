package com.redhat.labs.cache;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * This interface defines the contracts the event handler should abide by.
 * @author faisalmasood
 */
public interface EventHandler {

    public void handleEvent(Message<JsonObject> getAllProjectsMessage);
    public String getChannelName();
    public void setPersistenceStore(ResidencyDataStore residencyDataStore);
}
