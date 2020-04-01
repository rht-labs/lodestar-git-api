package com.redhat.labs.omp.models.gitlab;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitMultiple {

    private Integer id;
    private String branch;
    private String commitMessage;
    private String startBranch;
    private String startSha;
    private Integer startProject;
    private List<Action> actions;
    private String authorEmail;
    private String authorName;
    private Boolean stats;
    private Boolean force;

}
