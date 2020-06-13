package com.redhat.labs.omp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.redhat.labs.exception.FileNotFoundException;
import com.redhat.labs.omp.config.JsonMarshaller;
import com.redhat.labs.omp.mocks.MockGitLabService;
import com.redhat.labs.omp.models.gitlab.HookConfig;

import io.quarkus.runtime.StartupEvent;

class ConfigServiceTest {
    
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
        service.webHooksFile = "/schema/webhooks.yaml";
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
