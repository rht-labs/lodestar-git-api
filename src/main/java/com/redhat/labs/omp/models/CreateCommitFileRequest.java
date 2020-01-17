package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CreateCommitFileRequest implements Serializable {

    @JsonbProperty("action")
    public FileAction action;


    @JsonbProperty("filePath")
    public String filePath;

    @JsonbProperty("content")
    public Object content;

    // Change encoding to ‘base64’
    @JsonbProperty("encoding")
    public final String encoding = "base64";


    private byte[] base64Content;
    private String urlEncodedFilePath;

    public CreateCommitFileRequest(String filePath, String content){
        this(FileAction.create, filePath, content);

    }

    public CreateCommitFileRequest(FileAction action, String filePath, String content){
        this.action = action;
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
