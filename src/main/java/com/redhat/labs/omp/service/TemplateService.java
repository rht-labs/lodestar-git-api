package com.redhat.labs.omp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.redhat.labs.exception.FileNotFoundException;
import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.rest.client.GitLabService;

import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.vertx.axle.core.eventbus.EventBus;

@ApplicationScoped
public class TemplateService {

    @ConfigProperty(name = "templateRepositoryId", defaultValue = "9407")
    Integer templateRepositoryId;

    @ConfigProperty(name = "metaFileFolder", defaultValue = "schema")
    String metaFileFolder;

    @ConfigProperty(name = "metaFileName", defaultValue = "meta.dat")
    String metaFileName;

    @Inject
    @RestClient
    GitLabService gitLabService;

    @Inject
    FileService fileService;

    @Inject
    protected Engine quteEngine;

    @Inject
    EventBus bus;

    public List<File> getAllFilesFromTemplateInventory() {

        // get inventory file
        File inventoryFile = getTemplateInventoryFile();

        // parse and get all template files
        return getTemplateInventoryFiles(inventoryFile);

    }

    public File getTemplateInventoryFile() {

        // inventory file
        String inventoryFileName = metaFileFolder + "/" + metaFileName;

        // get inventory file
        Optional<File> optional = fileService.getFile(templateRepositoryId, inventoryFileName);

        if (!optional.isPresent()) {
            throw new FileNotFoundException("could not get template inventory file from gitlab.");
        }

        return optional.get();

    }

    public List<File> getTemplateInventoryFiles(File file) {

        List<File> templateFiles = new ArrayList<>();

        // parse template inventory file
        String[] lines = file.getContent().split("\\r?\\n");

        for (String line : lines) {

            String fileName = metaFileFolder + line.substring(1);

            Optional<File> optional = fileService.getFile(templateRepositoryId, fileName);
            if (!optional.isPresent()) {
                throw new FileNotFoundException("could not get template file '" + fileName + "' from gitlab.");
            }

            // decode file attributes
            File templateFile = optional.get();

            // add template file to list
            templateFiles.add(templateFile);

        }

        return templateFiles;

    }

    public void processTemplatesForEngagement(List<File> templateFiles, Engagement engagement) {

        templateFiles.parallelStream()
                .forEach(templateFile -> templateFile.setContent(processTemplate(templateFile, engagement)));

    }

    public String processTemplate(File file, Engagement engagement) {

        Template gitTemplate = quteEngine.parse(file.getContent());
        return gitTemplate.data("engagement", engagement).render();

    }

}
