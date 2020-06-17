package com.redhat.labs.omp.models.gitlab;

import javax.json.bind.annotation.JsonbProperty;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Commit  {

    private String id;
    @JsonbProperty("short_id")
    private String shortId;
    private String title;
    @JsonbProperty("author_name")
    private String authorName;
    @JsonbProperty("author_email")
    private String authorEmail;
    @JsonbProperty("committer_name")
    private String commiterName;
    @JsonbProperty("committer_email")
    private String commiterEmail;
    @JsonbProperty("authored_date")
    private String authoredDate;
    @JsonbProperty("committed_date")
    private String commitDate;
    private String message;
    @JsonbProperty("web_url")
    private String url;

}
