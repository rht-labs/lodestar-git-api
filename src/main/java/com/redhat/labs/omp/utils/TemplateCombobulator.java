package com.redhat.labs.omp.utils;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.labs.omp.models.filesmanagement.GetMultipleFilesResponse;
import com.redhat.labs.omp.resources.TemplateResource;
import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Template;

@ApplicationScoped
public class TemplateCombobulator {
    @Inject
    protected Engine quteEngine;

    @Inject
    protected TemplateResource quteTemplateResource;

    public TemplateInstance combobulateTemplateInstance(String fileContent, Map<String, Object> templateVariables) {
        // String should be the template
        Template fetchedTemplate = quteEngine.parse(fileContent);
        TemplateInstance processedTemplate = null;
        for (Map.Entry<String, Object> entry : templateVariables.entrySet()) {
            if (processedTemplate == null) {
                processedTemplate = fetchedTemplate.data(entry.getKey(), entry.getValue());
            } else {
                processedTemplate = processedTemplate.data(entry.getKey(), entry.getValue());
            }
        }
        return processedTemplate;
    }

    private String combobulateTemplateInstanceAsString(String templateName, Map<String, Object> templateVariables) {
        return this.combobulateTemplateInstance(templateName, templateVariables).render();
    }

    public GetMultipleFilesResponse process(Map<String, Object> templateVariables) {
        GetMultipleFilesResponse allTemplateFiles = quteTemplateResource.getAllFilesFromGit();
        allTemplateFiles.files.parallelStream()
                .forEach(singleFileResponse -> singleFileResponse.fileContent = combobulateTemplateInstanceAsString(singleFileResponse.getFileContent(),
                        templateVariables));
        return allTemplateFiles;
    }
}