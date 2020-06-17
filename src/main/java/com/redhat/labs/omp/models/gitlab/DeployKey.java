package com.redhat.labs.omp.models.gitlab;

import javax.json.bind.annotation.JsonbProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployKey {
    
    private String title;
    
    @JsonbProperty("can_push")
    private boolean canPush;

}
