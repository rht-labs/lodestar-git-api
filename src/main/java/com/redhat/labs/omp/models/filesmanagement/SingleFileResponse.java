package com.redhat.labs.omp.models.filesmanagement;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

/**
 * This class defines the single file content fetchged from Git.
 *
 */
public class SingleFileResponse implements Serializable {

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

    public String getFileContent(){
        return fileContent;
    }
}
