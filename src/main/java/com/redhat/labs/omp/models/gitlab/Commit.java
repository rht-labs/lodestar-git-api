package com.redhat.labs.omp.models.gitlab;

import javax.json.bind.annotation.JsonbProperty;

public class Commit extends File {

    @JsonbProperty("action")
    public FileAction action;

}
