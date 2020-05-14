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

        // encode contents
        if (null != content) {
            byte[] encodedContents = EncodingUtils
                    .base64Encode(this.content.getBytes(StandardCharsets.UTF_8.toString()));
            this.content = new String(encodedContents, StandardCharsets.UTF_8);
        }

    }

    public void decodeActionAttributes() throws UnsupportedEncodingException {

        // decode contents
        if (null != content) {
            byte[] decodedContents = EncodingUtils.base64Decode(this.content);
            this.content = new String(decodedContents, StandardCharsets.UTF_8);

        }

    }

}
