package com.redhat.labs.omp.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.cache.cacheStore.ResidencyDataCache;
import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.filesmanagement.GetMultipleFilesResponse;
import com.redhat.labs.omp.models.filesmanagement.SingleFileResponse;
import com.redhat.labs.omp.resources.TemplateResource;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;

@ApplicationScoped
public class TemplateCombobulator {
    private static Logger LOGGER = LoggerFactory.getLogger(TemplateCombobulator.class);

    @Inject
    protected Engine quteEngine;

    @Inject
    protected TemplateResource quteTemplateResource;

    @Inject
    protected ResidencyDataCache cache;

    private String combobulateTemplate(SingleFileResponse file, Engagement engagement) {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Content: {} Engagement {}", file.fileContent, engagement);
        }

        Template gitTemplate = quteEngine.parse(file.fileContent);
        return gitTemplate.data("engagement", engagement).render();
    }

    public GetMultipleFilesResponse process(Engagement engagement) {
        GetMultipleFilesResponse allTemplateFiles = quteTemplateResource.getAllFilesFromGit();
        allTemplateFiles.files.parallelStream()
                .forEach(singleFileResponse -> singleFileResponse.fileContent = combobulateTemplate(singleFileResponse,
                        engagement));
        return allTemplateFiles;
    }
}