package com.redhat.labs.omp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.redhat.labs.exception.FileNotFoundException;
import com.redhat.labs.omp.config.JsonMarshaller;
import com.redhat.labs.omp.mocks.MockGitLabService;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.models.gitlab.HookConfig;
import com.redhat.labs.utils.ResourceLoader;

import io.quarkus.runtime.StartupEvent;

class ConfigServiceTest {

    @Test void testGetConfigFilePreLoaded() {
        ConfigService service = new ConfigService();
        service.configFile = "src/test/resources/lodestar-runtime-config.yaml";
        service.webHooksFile = "src/test/resources/webhooks.yaml";
        service.marshaller = new JsonMarshaller();
        
        service.onStart(new StartupEvent());
        File config = service.getConfigFile();

        assertNotNull(config);
        assertEquals("src/test/resources/lodestar-runtime-config.yaml", config.getFilePath());
        assertEquals(ResourceLoader.load("lodestar-runtime-config.yaml"), config.getContent());

    }

    @Test void testGetConfigFileFromGitLab() {
        ConfigService service = new ConfigService();
        service.configFile = "runtime/lodestar-runtime-config.yaml";
        service.webHooksFile = "src/test/resources/webhooks.yaml";
        service.marshaller = new JsonMarshaller();

        FileService fileService = new FileService();
        fileService.gitLabService = new MockGitLabService();
        service.fileService = fileService;

        service.onStart(new StartupEvent());
        File config = service.getConfigFile();

        assertNotNull(config);
        assertEquals("runtime/lodestar-runtime-config.yaml", config.getFilePath());
        assertEquals(ResourceLoader.load("lodestar-runtime-config.yaml"), config.getContent());

    }

    @Test void testGetConfigFileNotFound() {
        ConfigService service = new ConfigService();
        service.configFile = "schema/notfound.yaml";
        
        FileService fileService = new FileService();
        fileService.gitLabService = new MockGitLabService();
        service.fileService = fileService;
        
        Exception exception = assertThrows(FileNotFoundException.class, () -> {
            service.getConfigFile();
        });
        
        assertEquals("the configured file was not found in the gitlab repository.", exception.getMessage());
    }
    
    @Test void testGetHookConfigPreLoaded() {
        ConfigService service = new ConfigService();
        service.configFile = "src/test/resources/config.yml";
        service.webHooksFile = "src/test/resources/webhooks.yaml";
        service.marshaller = new JsonMarshaller();
        
        service.onStart(new StartupEvent());
        List<HookConfig> hookConfigList = service.getHookConfig();
        
        assertNotNull(hookConfigList);
        assertEquals(2, hookConfigList.size());
        assertEquals("abc", hookConfigList.iterator().next().getToken());
        
    }
    
    @Test void testGetHookConfigLabNotEmpty() {
        ConfigService service = new ConfigService();
        service.configFile = "src/test/resources/config.yml";
        service.webHooksFile = "/runtime/webhooks.yaml";
        service.marshaller = new JsonMarshaller();
        
        FileService fileService = new FileService();
        fileService.gitLabService = new MockGitLabService();
        service.fileService = fileService;
        
        service.onStart(new StartupEvent());
        List<HookConfig> hookConfigList = service.getHookConfig();
        
        assertNotNull(hookConfigList);
        assertEquals(2, hookConfigList.size());
        assertEquals("abc", hookConfigList.iterator().next().getToken());
    }
    
    @Test void testGetHookConfigLabsEmpty() {
        ConfigService service = new ConfigService();
        service.configFile = "src/test/resources/config.yml";
        service.webHooksFile = "schema/notfound.yaml";
        service.marshaller = new JsonMarshaller();
        
        FileService fileService = new FileService();
        fileService.gitLabService = new MockGitLabService();
        service.fileService = fileService;
        
        service.onStart(new StartupEvent());
        List<HookConfig> hookConfigList = service.getHookConfig();
        
        assertNotNull(hookConfigList);
        assertEquals(0, hookConfigList.size());
    }
}
