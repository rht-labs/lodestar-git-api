package com.redhat.labs.omp.models.gitlab;

import javax.json.bind.annotation.JsonbProperty;

import com.redhat.labs.omp.models.FileAction;

public class Commit extends File {

    @JsonbProperty("action")
    public FileAction action;

}
