package com.redhat.labs.omp.models.gitlab.response;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Data;

@Data
public class GetMultipleFilesResponse {

    @JsonbProperty("files")
    public List<RepositoryFile> files;

}
