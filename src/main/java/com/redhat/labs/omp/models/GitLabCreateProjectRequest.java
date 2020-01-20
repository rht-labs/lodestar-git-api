package com.redhat.labs.omp.models;

import java.util.Date;

public class GitLabCreateProjectRequest {
    public String name;



   public Integer namespace_id;

    public String description = "Residency Created on " + new Date();
}
