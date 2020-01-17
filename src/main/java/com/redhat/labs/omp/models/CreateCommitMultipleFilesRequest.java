package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateCommitMultipleFilesRequest implements Serializable {

    @JsonbProperty("branch")
    public String branch;

    // Commit message
    @JsonbProperty("commit_message")
    public String commitMessage;



    // Specify the commit author’s email address
    @JsonbProperty("author_email")
    public String authorEmail;

    // Specify the commit author’s name
    @JsonbProperty("author_name")
    public String authorName;


    @JsonbProperty("actions")
    public CreateCommitFileRequest[] actions;

    private List<CreateCommitFileRequest> actionsList = new ArrayList<>();

    public void addFileRequest(CreateCommitFileRequest createCommitFileRequest){
        actionsList.add(createCommitFileRequest);
    }

    @JsonbTransient
    public CreateCommitFileRequest[] getActions(){
        return actionsList.toArray(new CreateCommitFileRequest[actionsList.size()]);

    }
}
