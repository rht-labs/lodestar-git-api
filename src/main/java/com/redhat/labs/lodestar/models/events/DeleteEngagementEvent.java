package com.redhat.labs.lodestar.models.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteEngagementEvent {

    private String customerName;
    private String engagementName;

}
