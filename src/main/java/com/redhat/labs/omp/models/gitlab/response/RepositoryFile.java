package com.redhat.labs.omp.models.gitlab.response;

import javax.json.bind.annotation.JsonbProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryFile {

    @JsonbProperty("fileName")
    private String fileName;

    @JsonbProperty("fileContent")
    private String fileContent;

    private String cacheKey;

    public void setCacheKey(String repoId, String branch) {
        cacheKey = String.format("%s:%s:%s", fileName, repoId, branch);
    }

}
