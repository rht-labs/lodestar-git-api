package com.redhat.labs.lodestar.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * A class to return an uuid - project id pair
 *
 */
public class EngagementProject {
    private static final String descriptionPrefixFormat = "engagement UUID: ";
    
    private long projectId;
    private String uuid;

    public static class EngagementProjectBuilder {
        public EngagementProjectBuilder uuid(String uuid) {
            if(uuid != null && uuid.startsWith(descriptionPrefixFormat)) {
                this.uuid = uuid.substring(descriptionPrefixFormat.length());
            }
            return this;
        }
    }
}

