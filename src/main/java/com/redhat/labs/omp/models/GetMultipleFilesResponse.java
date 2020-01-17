package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.List;

public class GetMultipleFilesResponse implements Serializable {
    @JsonbProperty("files")
    public List<SingleFileResponse> files;
}
