package com.redhat.labs.lodestar.models.events;

public class EventType {

    private EventType() {
        throw new IllegalStateException("Utility class");
    }

    public static final String DELETE_ENGAGEMENT_EVENT = "delete.engagement.event";
    public static final String DELETE_PROJECT_EVENT = "delete.project.event";
    public static final String CLEANUP_PROJECT_STRUCTURE_EVENT = "cleanup.project.structure.event";

}
