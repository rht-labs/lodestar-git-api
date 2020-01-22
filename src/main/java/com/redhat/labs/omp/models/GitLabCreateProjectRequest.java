package com.redhat.labs.omp.models;

import java.io.Serializable;
import java.util.Date;

public class GitLabCreateProjectRequest implements Serializable {
    public String name;

    public Integer namespace_id;

    public String description = "\uD83D\uDCA1 Residency Created on " + new Date() + " \uD83D\uDCA1";
}
