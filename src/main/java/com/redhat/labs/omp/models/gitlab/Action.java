package com.redhat.labs.omp.models.gitlab;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

import com.redhat.labs.omp.utils.EncodingUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    @NotBlank
    @JsonbProperty("action")
    private FileAction action;
    @NotBlank
    @JsonbProperty("file_path")
    private String filePath;
    @JsonbProperty("previous_path")
    private String previousPath;
    @JsonbProperty("content")
    private String content;
    @JsonbProperty("encoding")
    private String encoding;
    @JsonbProperty("last_commit_id")
    private String lastCommitId;
    @JsonbProperty("execute_filemode")
    private Boolean executeFileMode;

    public void encodeActionAttributes() throws UnsupportedEncodingException {

        // encode file path
        if (null != filePath) {
            String encodedFilePath = EncodingUtils.urlEncode(this.filePath);
            this.filePath = encodedFilePath;
        }

        // encode previous path
        if (null != previousPath) {
            String encodedFilePath = EncodingUtils.urlEncode(this.previousPath);
            this.previousPath = encodedFilePath;
        }

        // encode contents
        if (null != content) {
            byte[] encodedContents = EncodingUtils.base64Encode(this.filePath.getBytes());
            this.content = new String(encodedContents, StandardCharsets.UTF_8);
        }

    }

    public void decodeActionAttributes() throws UnsupportedEncodingException {

        // decode file path
        if (null != filePath) {
            String decodedFilePath = EncodingUtils.urlDecode(this.filePath);
            this.filePath = decodedFilePath;
        }

        // decode previous path
        if (null != previousPath) {
            String decodedFilePath = EncodingUtils.urlDecode(this.previousPath);
            this.previousPath = decodedFilePath;
        }

        // decode contents
        if (null != content) {
            byte[] decodedContents = EncodingUtils.base64Decode(this.content);
            this.content = new String(decodedContents, StandardCharsets.UTF_8);

        }

    }

}
