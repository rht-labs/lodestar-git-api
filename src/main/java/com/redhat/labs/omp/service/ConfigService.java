package com.redhat.labs.omp.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.redhat.labs.exception.FileNotFoundException;
import com.redhat.labs.omp.models.gitlab.File;

@ApplicationScoped
public class ConfigService {

    @ConfigProperty(name = "config.file", defaultValue = "schema/config.yml")
    String configFile;

    @ConfigProperty(name = "config.repository.id", defaultValue = "9407")
    String configRepositoryId;

    @Inject
    FileService fileService;

    public File getConfigFile() {

        Optional<File> optional = fileService.getFile(Integer.valueOf(configRepositoryId), configFile);

        if (!optional.isPresent()) {
            throw new FileNotFoundException("the configured file was not found in the gitlab repository.");
        }

        return optional.get();

    }

}
