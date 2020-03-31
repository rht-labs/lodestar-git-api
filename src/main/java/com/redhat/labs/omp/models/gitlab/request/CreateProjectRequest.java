package com.redhat.labs.omp.models.gitlab.request;

import javax.json.bind.annotation.JsonbProperty;

public class CreateProjectRequest {
    @JsonbProperty("project_name")
    public String projectName;
}