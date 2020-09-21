package com.redhat.labs.lodestar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.redhat.labs.lodestar.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.Status;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.Project;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class EngagementServiceTest {

    @Inject
    EngagementService engagementService;
    
    @Test void testCreateEngagementUpdateProject() {
        
        Engagement e = Engagement.builder().customerName("updated").projectName("updated2").build();
        Project project = engagementService.createEngagement(e, "Test Banana", "test@test.com", Optional.empty(), Optional.empty());
        assertFalse(project.isFirst());
            
    }
    
    @Test void testCreateEngagementGroupFail() {
        
        Engagement e = Engagement.builder().customerName("customer").projectName("project").build();
        Exception exception = assertThrows(UnexpectedGitLabResponseException.class, () -> {
            engagementService.createEngagement(e, "Test Banana", "test@test.com", Optional.empty(), Optional.empty());
        });
        
        assertEquals("failed to create group", exception.getMessage());
    }
    
    @Test void testGetEngagementByNamesapaceNotFound() {
        
        Engagement engagement = engagementService.getEngagement("blah", false);
        
        assertNull(engagement);
    }
    
    @Test void testCreateEngagementCommitFileFail() {
        
        Engagement e = Engagement.builder().customerName("project1").projectName("project1").build();
        Exception exception = assertThrows(UnexpectedGitLabResponseException.class, () -> {
            engagementService.createEngagement(e, "Test Banana", "fail@commitmultiplefiles.com", Optional.empty(), Optional.empty());
        });

        assertEquals("failed to commit files for engagement creation.", exception.getMessage());
    }
    
    @Test void testGetHooksNone() {
        List<Hook> hooks = engagementService.getHooks("nope", "nada");
        assertNotNull(hooks);
        assertEquals(0, hooks.size());
        
    }
    
    @Test void tesetNoStatus() {
        Status status = engagementService.getProjectStatus("nope", "nada");
        assertNull(status);
    }
}
