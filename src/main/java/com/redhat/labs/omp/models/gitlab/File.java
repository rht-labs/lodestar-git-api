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
public class File {

    @NotBlank
    @JsonbProperty("file_path")
    private String filePath;
    @NotBlank
    @JsonbProperty("branch")
    private String branch;
    @NotBlank
    @JsonbProperty("content")
    private String content;
    @NotBlank
    @JsonbProperty("commit_message")
    private String commitMessage;

    @JsonbProperty("start_branch")
    private String startBranch;
    @Builder.Default
    @JsonbProperty("encoding")
    private String encoding = "base64";
    @JsonbProperty("author_email")
    private String authorEmail;
    @JsonbProperty("author_name")
    private String authorName;

    public void encodeFileAttributes() throws UnsupportedEncodingException {

        // encode file path
        if (null != filePath) {
            String encodedFilePath = EncodingUtils.urlEncode(this.filePath);
            this.filePath = encodedFilePath;
        }

        // encode contents
        if (null != content) {
            byte[] encodedContents = EncodingUtils.base64Encode(this.filePath.getBytes());
            this.content = new String(encodedContents, StandardCharsets.UTF_8);
        }

    }

    public void decodeFileAttributes() throws UnsupportedEncodingException {

        // decode file path
        if (null != filePath) {
            String decodedFilePath = EncodingUtils.urlDecode(this.filePath);
            this.filePath = decodedFilePath;
        }

        // decode contents
        if (null != content) {
            byte[] decodedContents = EncodingUtils.base64Decode(this.content);
            this.content = new String(decodedContents, StandardCharsets.UTF_8);

        }

    }

}
