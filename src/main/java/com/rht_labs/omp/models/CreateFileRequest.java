package com.rht_labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import java.util.HashMap;
import java.util.Map;

public class CreateFileRequest {
    @JsonbProperty("project_id")
    public Integer projectId;

    @JsonbProperty("file_path")
    public String filePath;

    @JsonbProperty("content")
    public Object content;

    @JsonbProperty("branch")
    public String branch = "master";

    @JsonbProperty("comment")
    public String comment;

    @JsonbProperty("convert_to")
    public Conversion convertTo = Conversion.NONE;
}