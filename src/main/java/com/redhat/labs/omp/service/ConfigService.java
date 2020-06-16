package com.redhat.labs.omp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.exception.FileNotFoundException;
import com.redhat.labs.omp.config.JsonMarshaller;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.models.gitlab.HookConfig;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class ConfigService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    @ConfigProperty(name = "config.file")
    String configFile;
    
    @ConfigProperty(name = "webhook.file")
    String webHooksFile;

    @ConfigProperty(name = "config.repository.id", defaultValue = "9407")
    String configRepositoryId;
    
    @ConfigProperty(name = "config.gitlab.ref", defaultValue = "master")
    String gitRef;
    
    List<HookConfig> hookConfigList;

    @Inject
    FileService fileService;
    
    @Inject
    JsonMarshaller marshaller;
    
    void onStart(@Observes StartupEvent event) {
        hookConfigList = marshaller.fromYamlFile(webHooksFile, HookConfig.class);
        LOGGER.debug("Hook Config List {}", hookConfigList);
    }

    public File getConfigFile() {

        Optional<File> optional = fileService.getFile(configRepositoryId, configFile, gitRef);

        if (!optional.isPresent()) {
            throw new FileNotFoundException("the configured file was not found in the gitlab repository.");
        }

        return optional.get();
    }
    
    public List<HookConfig> getHookConfig() {

        if(hookConfigList != null) {
            return hookConfigList;
        }
        
        String gitLabHookFile = webHooksFile.charAt(0) == '/' ? webHooksFile.substring(1) : webHooksFile;
        Optional<File> optional = fileService.getFile(configRepositoryId, gitLabHookFile, gitRef);

        if (!optional.isPresent()) {
            LOGGER.error("No webhook file could be found. This is abnormal but not a deal breaker");
            return new ArrayList<>();
        }
        
        File file = optional.get();
       
        return marshaller.fromYaml(file.getContent(), HookConfig.class);
        
    }

}
