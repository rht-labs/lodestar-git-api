package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class CreateGroupRequest implements Serializable {

    @JsonbProperty("name")
    public String name;

    @JsonbProperty("path")
    public String path;

    @JsonbProperty("visibility")
    public String visibility = "private";

    @JsonbProperty("parent_id")
    public Integer parent_id = 3060;
}
