package com.rht_labs.cache;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public interface EventHandler {

    public   void handleEvent(Message<JsonObject> getAllProjectsMessage);
    public String getChannelName();
}
