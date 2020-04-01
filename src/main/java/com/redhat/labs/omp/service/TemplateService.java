package com.redhat.labs.omp.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.cache.GitSyncService;
import com.redhat.labs.cache.cacheStore.ResidencyDataCache;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.models.gitlab.response.GetMultipleFilesResponse;
import com.redhat.labs.omp.models.gitlab.response.RepositoryFile;
import com.redhat.labs.omp.rest.client.GitLabService;

import io.vertx.axle.core.eventbus.EventBus;

@ApplicationScoped
public class TemplateService {

    private static Logger LOGGER = LoggerFactory.getLogger(TemplateService.class);

    @ConfigProperty(name = "templateRepositoryId", defaultValue = "9407")
    Integer templateRepositoryId;

    @ConfigProperty(name = "metaFileFolder", defaultValue = "schema")
    String metaFileFolder;

    @ConfigProperty(name = "metaFileName", defaultValue = "schema")
    String metaFileName;

    @Inject
    @RestClient
    GitLabService gitLabService;

    @Inject
    FileService fileService;

    @Inject
    EventBus bus;

    @Inject
    ResidencyDataCache cache;

    public GetMultipleFilesResponse getAllFilesFromGit() {

//        // TODO cache this
//        // get template files from meta.dat file in git
//        File metaFile = fileService.getFile(templateRepositoryId, metaFileFolder + "/" + metaFileName);
//        // save file to cache
//        bus.publish(GitSyncService.FILE_CACHE_EVENT, metaFile);
//
//        // parse meta file for list of repository files
//        List<RepositoryFile> repositoryFiles = new ArrayList<>();
//
//        String[] lines = metaFile.getContent().split("\\r?\\n");
//        for (String line : lines) {
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("line " + " : " + metaFileFolder + line.substring(1));
//            }
//
//            // get file from git
//            RepositoryFile fileResponse = fileService.getFileFromRespository(metaFileFolder + line.substring(1),
//                    templateRepositoryId);
//            repositoryFiles.add(fileResponse);
//        }
//
//        GetMultipleFilesResponse getMultipleFilesResponse = new GetMultipleFilesResponse();
//        getMultipleFilesResponse.files = repositoryFiles;
//        return getMultipleFilesResponse;

        return null;
    }

}
