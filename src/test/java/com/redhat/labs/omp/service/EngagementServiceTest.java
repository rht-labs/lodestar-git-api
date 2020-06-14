package com.redhat.labs.omp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.redhat.labs.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.gitlab.Hook;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class EngagementServiceTest {

    @Inject
    EngagementService engagementService;
    
    @Test void testCreateEngagementGroupFail() {
        
        Engagement e = Engagement.builder().customerName("customer").projectName("project").build();
        Exception exception = assertThrows(UnexpectedGitLabResponseException.class, () -> {
            engagementService.createEngagement(e, "Test Banana", "test@test.com");
        });
        
        assertEquals("failed to create group", exception.getMessage());
    }
    
    @Test void testCreateEngagementCommitFileFail() {
        
        Engagement e = Engagement.builder().customerName("project1").projectName("project1").build();
        Exception exception = assertThrows(UnexpectedGitLabResponseException.class, () -> {
            engagementService.createEngagement(e, "Test Banana", "fail@commitmultiplefiles.com");
        });

        assertEquals("failed to commit files for engagement creation.", exception.getMessage());
    }
    
    @Test void testGetHooksNone() {
        List<Hook> hooks = engagementService.getHooks("nope", "nada");
        assertNotNull(hooks);
        assertEquals(0, hooks.size());
        
    }
}
