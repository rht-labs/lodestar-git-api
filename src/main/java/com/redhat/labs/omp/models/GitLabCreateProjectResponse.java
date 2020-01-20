package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GitLabCreateProjectResponse implements Serializable {

    @JsonbProperty("id")
    public Integer id;

    @JsonbProperty("description")
    public String description;

    @JsonbProperty("name")
    public String name;

    @JsonbProperty("path")
    public String path;
    /*private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }*/

}