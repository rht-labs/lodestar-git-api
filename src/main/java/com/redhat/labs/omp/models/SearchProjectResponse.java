package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class SearchProjectResponse implements Serializable {
    @JsonbProperty("id")
    public Integer id;

    @JsonbProperty("name")
    public String name;

    @JsonbProperty("path")
    public String path;
}
