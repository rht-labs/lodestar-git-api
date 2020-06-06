package com.redhat.labs.omp.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.omp.models.gitlab.CommitMultiple;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.rest.client.GitLabService;

@ApplicationScoped
public class FileService {
    public static Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    @Inject
    @RestClient
    GitLabService gitLabService;
    
    private static final String DEFAULT_REF = "master";
    

    // create a file
    public Optional<File> createFile(Integer projectId, String filePath, File file) {

        Optional<File> optional = Optional.empty();

        // encode before sending to gitlab
        file.encodeFileAttributes();

        // create new file
        File createdFile = gitLabService.createFile(projectId, filePath, file);

        // decode file after creation
        file.decodeFileAttributes();

        if (null != createdFile) {
            optional = Optional.of(createdFile);
        }

        return optional;

    }

    // create multiple files
    public boolean createFiles(Integer projectId, CommitMultiple commit) {

        Response response = null;

        // encode actions in commit
        commit.encodeActions();

        // call gitlab api to commit
        response = gitLabService.commitMultipleFiles(projectId, commit);

        // decode actions in commit
        commit.decodeActions();

        // should get a 201 back if commit created
        return HttpStatus.SC_CREATED == response.getStatus();
    }

    // update a file
    public Optional<File> updateFile(Integer projectId, String filePath, File file) {

        Optional<File> optional = Optional.empty();

        // encode file
        file.encodeFileAttributes();

        // update file
        File updatedFile = gitLabService.updateFile(projectId, filePath, file);

        // decode file
        file.decodeFileAttributes();

        if (null != updatedFile) {
            optional = Optional.of(updatedFile);
        }

        return optional;

    }

    // delete a file
    public Optional<File> deleteFile(Integer projectId, String filePath) {
        return deleteFile(projectId, filePath, DEFAULT_REF);
    }

    public Optional<File> deleteFile(Integer projectId, String filePath, String ref) {

        Optional<File> optional = getFile(projectId, filePath, ref, false);

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
        return getFile(projectId, filePath, DEFAULT_REF, false);
    }

    // get a file
    public Optional<File> getFileAllow404(Integer projectId, String filePath) {
        return getFile(projectId, filePath, DEFAULT_REF, true);
    }

    // get a file
    public Optional<File> getFile(Integer projectId, String filePath, String ref) {
        return getFile(projectId, filePath, ref, false);
    }

    public Optional<File> getFile(Integer projectId, String filePath, String ref, boolean allow404) {

        Optional<File> optional = Optional.empty();

        try {

            // get file
            File file = gitLabService.getFile(projectId, filePath, ref);

            if (null != file) {
                // decode file
                file.decodeFileAttributes();
                optional = Optional.of(file);
            }
        } catch(WebApplicationException wae) {
            if(wae.getResponse().getStatus() != 404) {
                LOGGER.error("Get file {} for project {} failed with code {}", filePath, projectId, wae.getResponse().getStatus());
                throw wae;
            } else if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Get file {} for project {} failed with code {}", filePath, projectId, wae.getResponse().getStatus());
            }
        }

        return optional;

    }

}
