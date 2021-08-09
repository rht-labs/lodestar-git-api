package com.redhat.labs.lodestar.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Artifact extends EngagementAttribute {

    private String title;
    private String description;
    private String type;
    private String linkAddress;
    private String region; //migration support - not available in v1

}
