package com.redhat.labs.omp.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.redhat.labs.omp.models.gitlab.HookConfig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JsonMarshallerTest {
    
    @Inject
    JsonMarshaller marshaller;
    
    @Test
    public void testHookConfigLoad() {
        List<HookConfig> config = marshaller.fromYamlFile("src/test/resources/webhooks.yaml", HookConfig.class);
        
        assertNotNull(config);
        assertEquals(2,  config.size());
        
        HookConfig hookConfig = config.iterator().next();
        assertEquals("labs", hookConfig.getName());
        assertTrue(hookConfig.isPushEvent());
        assertEquals("https://labs.com/webhooks/", hookConfig.getBaseUrl());
        assertEquals("master", hookConfig.getPushEventsBranchFilter());
        assertEquals("abc", hookConfig.getToken());
    }
    
    @Test
    public void testInvalidYamlNullReturn() {
        List<HookConfig> hookList = marshaller.fromYamlFile("src/test/resources/meta.dat", HookConfig.class);
        assertNull(hookList);
    }

}
