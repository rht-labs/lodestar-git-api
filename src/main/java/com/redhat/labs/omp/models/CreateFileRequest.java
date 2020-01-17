package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;

public class CreateFileRequest {
    @JsonbProperty("project_id")
    public String projectId;

    @JsonbProperty("file_path")
    public String filePath;

    @JsonbProperty("content")
    public Object content;

    @JsonbProperty("branch")
    public String branch = "master";

    @JsonbProperty("comment")
    public String comment;

    @JsonbProperty("output_format")
    public OutputFormat outputFormat = OutputFormat.ORIGINAL;
}