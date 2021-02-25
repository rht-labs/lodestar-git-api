package com.redhat.labs.lodestar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.gradle.internal.impldep.com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.redhat.labs.lodestar.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.Status;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.rest.client.GitLabService;
import com.redhat.labs.lodestar.utils.MockUtils;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
class EngagementServiceTest {

    @Inject
    EngagementService engagementService;

    @InjectMock
    @RestClient
    GitLabService gitLabService;

   @BeforeEach
    void setup() {

        // set the engagement path prefix
        Group g = Group.builder().fullPath("top/dog").build();
        MockUtils.setGetGroupByIdOrPathMock(gitLabService, 2, g);

    }

    @Test void testCreateEngagementNewProject() {

        // given
        Group customerGroup = MockUtils.mockCustomerGroup("new");
        Group projectGroup = MockUtils.mockProjectGroup("new2");
        Project iacProject = MockUtils.mockIacProject();

        // no project exists with id or customer/project name combo
        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(null);
        given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.any(Integer.class), Mockito.any(Integer.class)))
                .willReturn(Response.ok(Arrays.asList()).header("X-Total-Pages", 1).build());

        // create groups
        given(gitLabService.createGroup(Mockito.any(Group.class))).willReturn(customerGroup, projectGroup);
        given(gitLabService.createProject(Mockito.any(Project.class))).willReturn(iacProject);

        // make file commit succeed
        given(gitLabService.commitMultipleFiles(Mockito.anyInt(), Mockito.any()))
                .willReturn(Response.status(201).build());
        // no webhooks
        given(gitLabService.getFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).willReturn(null);

        Engagement e = Engagement.builder().customerName("new").projectName("new2").build();
        Project project = engagementService.createEngagement(e, "Test Banana", "test@test.com");
        assertTrue(project.isFirst());

    }

    @Test void testCreateEngagementUpdateProject() {

        // given
        Group customerGroup = MockUtils.mockCustomerGroup("updated");
        Group projectGroup = MockUtils.mockProjectGroup("updated2");
        Project iacProject = MockUtils.mockIacProject();

        // all projects and groups exist, no customer group subgroups
        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(iacProject);
        given(gitLabService.getGroupByIdOrPath(String.valueOf(MockUtils.PROJECT_GROUP_ID))).willReturn(projectGroup);
        given(gitLabService.getGroupByIdOrPath(String.valueOf(MockUtils.CUSTOMER_GROUP_ID))).willReturn(customerGroup);
        given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.any(Integer.class), Mockito.any(Integer.class)))
                .willReturn(Response.ok(Arrays.asList()).header("X-Total-Pages", 1).build());

        // make file commit succeed
        given(gitLabService.commitMultipleFiles(Mockito.anyInt(), Mockito.any()))
                .willReturn(Response.status(201).build());
        // no webhooks
        given(gitLabService.getFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).willReturn(null);

        Engagement e = Engagement.builder().projectId(MockUtils.PROJECT_ID).customerName("updated").projectName("updated2")
                .build();
        Project project = engagementService.createEngagement(e, "Test Banana", "test@test.com");
        assertFalse(project.isFirst());

    }

    @Test void testCreateEngagementGroupFail() {

        // given

        // no project exists with id or customer/project name combo
        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(null);
        given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.any(Integer.class), Mockito.any(Integer.class)))
                .willReturn(Response.ok(Arrays.asList()).header("X-Total-Pages", 1).build());

        // create groups
        given(gitLabService.createGroup(Mockito.any(Group.class))).willReturn(null);

        Engagement e = Engagement.builder().customerName("customer").projectName("project").build();
        Exception exception = assertThrows(WebApplicationException.class, () -> {
            engagementService.createEngagement(e, "Test Banana", "test@test.com");
        });
        
        assertEquals("failed to create group for customer", exception.getMessage());
    }

    @Test void testGetEngagementByNamesapaceNotFound() {
        
        given(gitLabService.getProjectById("blah")).willReturn(null);
        
        Engagement engagement = engagementService.getEngagement("blah", false);
        
        assertNull(engagement);
    }

    @Test void testCreateEngagementCommitFileFail() {

        // given
        Group customerGroup = MockUtils.mockCustomerGroup("project1");
        Group projectGroup = MockUtils.mockProjectGroup("project1");
        Project iacProject = MockUtils.mockIacProject();

        // no project exists with id or customer/project name combo
        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(null);
        given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.any(Integer.class), Mockito.any(Integer.class)))
                .willReturn(Response.ok(Arrays.asList()).header("X-Total-Pages", 1).build());

        // create groups
        given(gitLabService.createGroup(Mockito.any(Group.class))).willReturn(customerGroup, projectGroup);
        given(gitLabService.createProject(Mockito.any(Project.class))).willReturn(iacProject);

        // make file commit succeed
        given(gitLabService.commitMultipleFiles(Mockito.anyInt(), Mockito.any()))
                .willReturn(Response.status(400).build());

        Engagement e = Engagement.builder().customerName("project1").projectName("project1").build();
        Exception exception = assertThrows(UnexpectedGitLabResponseException.class, () -> {
            engagementService.createEngagement(e, "Test Banana", "fail@commitmultiplefiles.com");
        });

        assertEquals("failed to commit files for engagement creation.", exception.getMessage());
    }

    @Test void testGetHooksNone() {

        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(MockUtils.mockIacProject());
        given(gitLabService.getProjectHooks(MockUtils.PROJECT_ID)).willReturn(Arrays.asList());

        List<Hook> hooks = engagementService.getHooks("nope", "nada");
        assertNotNull(hooks);
        assertEquals(0, hooks.size());
        
    }

    @Test void testGetHooksProjectNotFound() {

        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(null);

        List<Hook> hooks = engagementService.getHooks("nope", "nada");
        assertNotNull(hooks);
        assertEquals(0, hooks.size());

    }

    @Test void tesetNoStatus() {

        Response r = Response.ok(Lists.newArrayList()).build();
        
        given(gitLabService.getProjectTree(Mockito.anyString(), Mockito.anyBoolean())).willReturn(r);
        given(gitLabService.getFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).willReturn(null);

        Optional<Status> status = engagementService.getProjectStatus("nope", "nada");

        assertTrue(status.isEmpty());

    }

}
