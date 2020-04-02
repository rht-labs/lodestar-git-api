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
public class ProjectSearchResults {

    @JsonbProperty("id")
    private Integer id;
    @JsonbProperty("description")
    private String description;
    @JsonbProperty("name")
    private String name;
    @JsonbProperty("path")
    private String path;
    @JsonbProperty("namespace")
    private Namespace namespace;

}
