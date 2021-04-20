package com.redhat.labs.lodestar.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EngagementAttribute {

    private String uuid;
    private String created;
    private String updated;
    private String engagementUuid;

}
