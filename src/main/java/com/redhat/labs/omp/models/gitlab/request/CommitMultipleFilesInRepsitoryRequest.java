package com.redhat.labs.omp.models.gitlab.request;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This request captures the GitLAb API json payload that is required to do the ommit of multiple files.
 *
 * required fields are id, branch and commit_messafge
 */
public class CommitMultipleFilesInRepsitoryRequest implements Serializable {

    public CommitMultipleFilesInRepsitoryRequest(){

    }

    @JsonbProperty("branch")
    public String branch = "master";

    // Commit message
    @JsonbProperty("commit_message")
    public String commitMessage = "dfault commit message";



    // Specify the commit author’s email address
    @JsonbProperty("author_email")
    public String authorEmail;

    // Specify the commit author’s name
    @JsonbProperty("author_name")
    public String authorName;




    @JsonbProperty("actions")
    public CreateCommitFileRequest[] actions;

    @JsonbTransient
    private List<CreateCommitFileRequest> actionsList = new ArrayList<>();

    public void addFileRequest(CreateCommitFileRequest createCommitFileRequest){
        actionsList.add(createCommitFileRequest);
    }

    @JsonbTransient
    public List<CreateCommitFileRequest> getActionsList(){
        return actionsList;

    }

    public CreateCommitFileRequest[] getActions(){
        return actionsList.toArray(new CreateCommitFileRequest[actionsList.size()]);

    }



    @Override
    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder();
        if(this.getActions() != null){
            for(CreateCommitFileRequest commitFileRequest : this.getActions()) {
                toStringBuilder.append(" Auth: ");
                toStringBuilder.append(commitFileRequest.filePath);
                toStringBuilder.append("\n");
            }
        }

        return toStringBuilder.toString();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitMultipleFilesInRepsitoryRequest that = (CommitMultipleFilesInRepsitoryRequest) o;
        return Objects.equals(branch, that.branch) &&
                Objects.equals(commitMessage, that.commitMessage) &&
                Objects.equals(authorEmail, that.authorEmail) &&
                Objects.equals(authorName, that.authorName) &&
                Objects.equals(actionsList, that.actionsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branch, commitMessage, authorEmail, authorName, actionsList);
    }
}
