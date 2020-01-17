package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class GetFileResponse implements Serializable {
    @JsonbProperty("file_name")
    public String fileName;

    @JsonbProperty("file_path")
    public String filePath;

    @JsonbProperty("content")
    public String content;

    @JsonbProperty("encoding")
    public String encoding;

    @JsonbProperty("size")
    public Integer size;




}
