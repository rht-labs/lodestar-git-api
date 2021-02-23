package com.redhat.labs.lodestar.models.gitlab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTreeNode {

    private String id;
    private String name;
    private String type;
    private String path;
    private String mode;

}
