package com.redhat.labs.lodestar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

import org.junit.jupiter.api.Test;

import com.redhat.labs.lodestar.config.JsonMarshaller;
import com.redhat.labs.lodestar.mocks.MockGitLabService;
import com.redhat.labs.lodestar.models.gitlab.HookConfig;
import com.redhat.labs.lodestar.rest.client.GitLabService;

class ConfigServiceTest {
    
    @Test void testGetHookConfigPreLoaded() {
        ConfigService service = new ConfigService();
        service.webHooksFile = "src/test/resources/webhooks.yaml";
        service.marshaller = new JsonMarshaller();

        ProjectService projectService = new ProjectService();
        projectService.gitLabService = new MockGitLabService();
        service.projectService = projectService;        
        service.engagementRepositoryId = 0;

        service.reloadConfig = true;
        service.reloadConfigMapData();
        List<HookConfig> hookConfigList = service.getHookConfig();
        
        assertNotNull(hookConfigList);
        assertEquals(2, hookConfigList.size());
        assertEquals("abc", hookConfigList.iterator().next().getToken());
        
    }
    
    @Test void testGetHookConfigLabNotEmpty() {
        ConfigService service = new ConfigService();
        service.webHooksFile = "/runtime/webhooks.yaml";
        service.marshaller = new JsonMarshaller();
        
        FileService fileService = new FileService();
        fileService.gitLabService = new MockGitLabService();
        service.fileService = fileService;
        
        service.reloadConfigMapData();
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
        
        service.reloadConfigMapData();
        List<HookConfig> hookConfigList = service.getHookConfig();
        
        assertNotNull(hookConfigList);
        assertEquals(0, hookConfigList.size());
    }

    @Test void testUpdateWebhooksInGitLab() {

        JsonbConfig config = new JsonbConfig()
                .withFormatting(true)
                .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        JsonMarshaller jm = new JsonMarshaller();
        jm.setJsonb(JsonbBuilder.create(config));

        ConfigService service = new ConfigService();
        service.webHooksFile = "src/test/resources/webhooks.yaml";
        service.marshaller = jm;

        GitLabService gitLabService = new MockGitLabService();

        FileService fileService = new FileService();
        fileService.gitLabService = gitLabService;
        service.fileService = fileService;

        ProjectService projectService = new ProjectService();
        projectService.gitLabService = gitLabService;
        service.projectService = projectService;        
        service.engagementRepositoryId = 200;

        EngagementService engagementService = new EngagementService();
        engagementService.fileService = fileService;
        engagementService.json = jm;
        engagementService.projectService = projectService;
        service.engagementService = engagementService;

        HookService hookService = new HookService();
        hookService.gitLabService = gitLabService;
        service.hookService = hookService;

        service.loadWebHookData();
        List<HookConfig> hookConfigList = service.getHookConfig();

        assertNotNull(hookConfigList);
        assertEquals(2, hookConfigList.size());

    }
}
