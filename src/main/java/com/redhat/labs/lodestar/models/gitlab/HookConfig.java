package com.redhat.labs.lodestar.models.gitlab;

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
    
    @JsonbProperty("pushEvent")
    private boolean pushEvent;
    
    @JsonbProperty("pushEventsBranchFilter")
    private String pushEventsBranchFilter;
    
    @JsonbProperty("token")
    private String token;
    
    /**
     * Should the webhook be enabled after an engagement is archived
     */
    @JsonbProperty("enabledAfterArchive")
    private boolean enabledAfterArchive;

}
