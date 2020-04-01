package com.redhat.labs.omp.service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.redhat.labs.omp.models.gitlab.CommitMultiple;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.rest.client.GitLabService;
import com.redhat.labs.omp.utils.EncodingUtils;

@ApplicationScoped
public class FileService {

    @Inject
    @RestClient
    GitLabService gitLabService;

    // create a file
    public Optional<File> createFile(Integer projectId, String filePath, File file) {

        Optional<File> optional = Optional.empty();

        try {
            // encode before sending to gitlab
            EncodingUtils.encodeFile(file);

            // create new file
            File createdFile = gitLabService.createFile(projectId, filePath, file);

            // decode file after creation
            EncodingUtils.decodeFile(createdFile);

            if (null != createdFile) {
                optional = Optional.of(createdFile);
            }

        } catch (UnsupportedEncodingException e) {
            return optional;
        }

        return optional;

    }

    // create multiple files
    public boolean createFiles(Integer projectId, CommitMultiple commit) {

        Response response = gitLabService.commitMultipleFiles(projectId, commit);

        if (HttpStatus.SC_CREATED == response.getStatus()) {
            return true;
        }

        return false;

    }

    // update a file
    public Optional<File> updateFile(Integer projectId, String filePath, File file) {

        Optional<File> optional = Optional.empty();

        try {

            // encode file
            EncodingUtils.encodeFile(file);

            // update file
            File updatedFile = gitLabService.updateFile(projectId, filePath, file);

            // decode file
            EncodingUtils.decodeFile(updatedFile);

            if (null != updatedFile) {
                optional = Optional.of(updatedFile);
            }

        } catch (UnsupportedEncodingException e) {
            return optional;
        }

        return optional;

    }

    // delete a file
    public Optional<File> deleteFile(Integer projectId, String filePath) {
        return deleteFile(projectId, filePath, "master");
    }

    public Optional<File> deleteFile(Integer projectId, String filePath, String ref) {

        Optional<File> optional = getFile(projectId, filePath, ref);

        if (optional.isPresent()) {

            // get the file
            File file = optional.get();
            // set branch
            file.setBranch(ref);
            // add commit message
            file.setCommitMessage("git api deleted file.");

            gitLabService.deleteFile(projectId, filePath, file);
        }

        return optional;
    }

    // get a file
    public Optional<File> getFile(Integer projectId, String filePath) {
        return getFile(projectId, filePath, "master");
    }

    public Optional<File> getFile(Integer projectId, String filePath, String ref) {

        Optional<File> optional = Optional.empty();

        try {

            // get file
            File file = gitLabService.getFile(projectId, filePath, ref);

            if (null != file) {

                // decode file
                EncodingUtils.decodeFile(file);

                optional = Optional.of(file);

            }

        } catch (UnsupportedEncodingException e) {
            // TODO: This should throw exception
            return optional;
        }

        return optional;

    }

}
