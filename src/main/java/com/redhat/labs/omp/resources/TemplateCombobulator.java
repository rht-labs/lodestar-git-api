package com.redhat.labs.omp.resources;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.labs.omp.models.filesmanagement.GetMultipleFilesResponse;
import io.quarkus.qute.Engine;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Template;

@ApplicationScoped
public class TemplateCombobulator  {

    @Inject
    Engine engine;

    @Inject
    TemplateResource templateResource;

    public TemplateInstance combobulateTemplateInstance(String fileContent, Map<String, Object> templateVariables) {


        // String should be the template
        Template fetchedTemplate = engine.parse(fileContent);
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

    public String combobulateTemplateInstanceAsString(String templateName, Map<String, Object> templateVariables) {
        return this.combobulateTemplateInstance(templateName, templateVariables).render();
    }

    public GetMultipleFilesResponse process( Map<String, Object> templateVariables) {
//        1. Process should take a map of vars from frontend
        GetMultipleFilesResponse allTemplateFiles = templateResource.getAllFilesFromGit();
        allTemplateFiles.files.parallelStream().forEach(singleFileResponse -> singleFileResponse.fileContent = combobulateTemplateInstanceAsString(singleFileResponse.getFileContent(), templateVariables));
        return allTemplateFiles;
     }
}