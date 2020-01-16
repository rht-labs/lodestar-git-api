package com.rht_labs.omp.models;

import java.util.HashMap;
import java.util.Map;

public class GitLabCreateProjectResponse {
    public Integer id;
    public Object description;
    public String name;
    public String path;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}