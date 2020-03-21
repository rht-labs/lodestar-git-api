package com.redhat.labs.omp.models.filesmanagement;

import javax.json.bind.annotation.JsonbProperty;

/**
 * This class defines the single file content fetchged from Git.
 *
 */
public class SingleFileResponse {

    public SingleFileResponse(){

    }

    public SingleFileResponse(String fileName, String fileContent){
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    @JsonbProperty("fileName")
    public  String fileName;

    @JsonbProperty("fileContent")
    public  String fileContent;

    public String cacheKey;

    public void setCacheKey(String repoId, String branch) {
        cacheKey = String.format("%s:%s:%s", fileName, repoId, branch);
    }

    public String getFileContent(){
        return fileContent;
    }

    public String toString() {
        return String.format("Single FileResponse: name: %s contents: %s", fileName, fileContent);
    }
}
