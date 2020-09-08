package com.redhat.labs.lodestar.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Launch {

    private String launchedDateTime;
    private String launchedBy;
    private String launchedByEmail;

}
