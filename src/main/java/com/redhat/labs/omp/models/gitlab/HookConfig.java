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
public class HookConfig {
    
    private String name;
    
    @JsonbProperty("baseUrl")
    private String baseUrl;
    
    @JsonbProperty("pushDvent")
    private boolean pushEvent;
    
    @JsonbProperty("pushEventsBranchFilter")
    private String pushEventsBranchFilter;
    
    @JsonbProperty("token")
    private String token;

}
