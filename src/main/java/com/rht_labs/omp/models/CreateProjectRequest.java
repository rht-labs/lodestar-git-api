package com.rht_labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;

public class CreateProjectRequest {
    @JsonbProperty("residency_name")
    public String residencyName;

    @JsonbProperty("cluster_name")
    public String clusterName;

    @JsonbProperty("region")
    public String region;
}