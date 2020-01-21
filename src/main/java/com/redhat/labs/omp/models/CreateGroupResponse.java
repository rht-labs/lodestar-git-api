package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class CreateGroupResponse implements Serializable {

    @JsonbProperty("id")
    public Integer id;

    @JsonbProperty("name")
    public String name;

    @JsonbProperty("parent_id")
    public Integer parent_id;

}
