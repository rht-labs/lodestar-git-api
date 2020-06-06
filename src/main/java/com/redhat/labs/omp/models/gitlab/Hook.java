package com.redhat.labs.omp.models.gitlab;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hook {

    private Integer id;
    
    private String url;
    
    private Integer projectId;
    
    private Boolean pushEvents;
    
    private String pushEventsBranchFilter;
    
    private Date createdAt;
    
    //Token not provide in GET requests
    private String token;
    
}
