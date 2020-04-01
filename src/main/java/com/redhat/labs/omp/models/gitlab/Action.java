package com.redhat.labs.omp.models.gitlab;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    @NotBlank
    @JsonbProperty("action")
    private String action;
    @NotBlank
    @JsonbProperty("file_path")
    private String filePath;
    @JsonbProperty("previous_path")
    private String previousPath;
    @JsonbProperty("content")
    private String content;
    @JsonbProperty("encoding")
    private String encoding;
    @JsonbProperty("last_commit_id")
    private String lastCommitId;
    @JsonbProperty("execute_filemode")
    private Boolean executeFileMode;

}
