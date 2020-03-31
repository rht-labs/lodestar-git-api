package com.redhat.labs.omp.models.gitlab.request;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GitLabCreateFileInRepositoryRequest {
    // Name of the branch
    @JsonbProperty("branch")
    public String branch;

    // Name of the branch to start the new commit from
    @JsonbProperty("start_branch")
    public String startBranch;

    // Commit message
    @JsonbProperty("commit_message")
    public String commitMessage;

    // Specify the commit author’s email address
    @JsonbProperty("author_email")
    public String authorEmail;

    // Specify the commit author’s name
    @JsonbProperty("author_name")
    public String authorName;

    // Change encoding to ‘base64’
    @JsonbProperty("encoding")
    public final String encoding = "base64";

    private byte[] base64Content;
    private String urlEncodedFilePath;

    public GitLabCreateFileInRepositoryRequest(String filePath, String branch, String commitMessage, byte[] content) {
        this.branch = branch;
        this.commitMessage = commitMessage;
        setContent(content);
        setFilePath(filePath);
    }

    public void setContent(byte[] content) {
        base64Content = Base64.getEncoder().encode(content);
    }

    public void setContent(String content) {
        setContent(content.getBytes(StandardCharsets.UTF_8));
    }

    // File content
    @JsonbProperty("content")
    public String getBase64ContentAsString() {
        return new String(base64Content, StandardCharsets.UTF_8);
    }

    @JsonbTransient
    public String getContent() {
        return new String(Base64.getDecoder().decode(base64Content), StandardCharsets.UTF_8);
    }

    // Url encoded full path to new file. Ex. lib%2Fclass%2Erb
    @JsonbProperty("file_path")
    public String getUrlEncodedFilePath() {
        return urlEncodedFilePath;
    }

    @JsonbTransient
    public String getFilePath() {
        try {
            return URLDecoder.decode(urlEncodedFilePath, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFilePath(String filePath) {
        try {
            urlEncodedFilePath = URLEncoder.encode(filePath, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
