package com.redhat.labs.lodestar.models.gitlab;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.redhat.labs.lodestar.exception.EncodingException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitMultiple {

    @NotNull
    private Integer id;
    @NotBlank
    private String branch;
    @NotBlank
    @JsonbProperty("commit_message")
    private String commitMessage;
    @JsonbProperty("start_branch")
    private String startBranch;
    @JsonbProperty("start_sha")
    private String startSha;
    @JsonbProperty("start_project")
    private Integer startProject;
    @NotNull
    private List<Action> actions;
    @JsonbProperty("author_email")
    private String authorEmail;
    @JsonbProperty("author_name")
    private String authorName;
    private Boolean stats;
    private Boolean force;

    public void encodeActions() {

        this.actions.stream().forEach(action -> {
            try {
                action.encodeActionAttributes();
            } catch (UnsupportedEncodingException e) {
                throw new EncodingException("failed to encode action attributes. " + action, e);
            }
        });

    }

    public void decodeActions() {
        this.actions.stream().forEach(Action::decodeActionAttributes);
    }

}
