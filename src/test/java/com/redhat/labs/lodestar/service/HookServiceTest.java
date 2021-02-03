package com.redhat.labs.lodestar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import com.redhat.labs.lodestar.models.gitlab.Hook;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class HookServiceTest {

    @Inject
    HookService hookService;
    
    @Test
    void testGetProjectHooks() {
        List<Hook> hookList = hookService.getProjectHooks(99);
        
        assertNotNull(hookList);
        assertEquals(1, hookList.size());
        assertEquals(13, hookList.get(0).getId());
    }
    
    @Test
    void testNewProjectHook() {
        Hook hook = Hook.builder().projectId(66).pushEvents(true).pushEventsBranchFilter("master").url("http://banana/hook")
                .token("token").build();
        Response response = hookService.createOrUpdateProjectHook(66, hook);
        assertEquals(201,  response.getStatus());
    }
    
    @Test
    void testUpdateProjectHook() {
        Hook hook = Hook.builder().projectId(99).pushEvents(true).pushEventsBranchFilter("master").url("http://webhook.edu/hook")
                .token("token").build();
        Response response = hookService.createOrUpdateProjectHook(hook.getProjectId(), hook);
        assertEquals(200,  response.getStatus());
    }
}
