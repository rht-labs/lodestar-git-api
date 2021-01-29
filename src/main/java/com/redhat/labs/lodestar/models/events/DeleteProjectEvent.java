package com.redhat.labs.lodestar.models.events;

import com.redhat.labs.lodestar.models.Engagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProjectEvent {

    private String engagementPathPrefix;
    private Engagement engagement;

}