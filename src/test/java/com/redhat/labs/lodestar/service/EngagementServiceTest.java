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

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.redhat.labs.lodestar.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.Status;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.Namespace;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.rest.client.GitLabService;
import com.redhat.labs.lodestar.utils.GitLabPathUtils;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class EngagementServiceTest {

    @Inject
    EngagementService engagementService;

    @InjectMock
    @RestClient
    GitLabService gitLabService;

    // create engagement new project - DONE
    // create engagement update project - DONE
    // create engagement group fail
    // engagement by namespace not found
    // commit file fail
    // get hooks none
    // test no status

    Integer repoId = 2;
    Integer customerGroupId = 2222;
    Integer projectGroupId = 3333;
    Integer projectId = 4444;

    @Test void testCreateEngagementNewProject() {

        // given
        Group customerGroup = mockCustomerGroup("new");
        Group projectGroup = mockProjectGroup("new2");
        Project iacProject = mockIacProject();

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
        Group customerGroup = mockCustomerGroup("updated");
        Group projectGroup = mockProjectGroup("updated2");
        Project iacProject = mockIacProject();

        // all projects and groups exist, no customer group subgroups
        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(iacProject);
        given(gitLabService.getGroupByIdOrPath(String.valueOf(projectGroupId))).willReturn(projectGroup);
        given(gitLabService.getGroupByIdOrPath(String.valueOf(customerGroupId))).willReturn(customerGroup);
        given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.any(Integer.class), Mockito.any(Integer.class)))
                .willReturn(Response.ok(Arrays.asList()).header("X-Total-Pages", 1).build());

        // make file commit succeed
        given(gitLabService.commitMultipleFiles(Mockito.anyInt(), Mockito.any()))
                .willReturn(Response.status(201).build());
        // no webhooks
        given(gitLabService.getFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).willReturn(null);

        Engagement e = Engagement.builder().projectId(projectId).customerName("updated").projectName("updated2")
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
        Group customerGroup = mockCustomerGroup("project1");
        Group projectGroup = mockProjectGroup("project1");
        Project iacProject = mockIacProject();

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

        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(mockIacProject());
        given(gitLabService.getProjectHooks(projectId)).willReturn(Arrays.asList());

        List<Hook> hooks = engagementService.getHooks("nope", "nada");
        assertNotNull(hooks);
        assertEquals(0, hooks.size());
        
    }

    @Test void tesetNoStatus() {

        given(gitLabService.getFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).willReturn(null);

        Status status = engagementService.getProjectStatus("nope", "nada");
        assertNull(status);
    }

    private Group mockCustomerGroup(String customerName) {
        return mockGroup(customerName, customerGroupId, repoId);
    }

    private Group mockProjectGroup(String projectName) {
        return mockGroup(projectName, projectGroupId, customerGroupId);
    }

    private Project mockIacProject() {
        return mockProject(projectId, "iac", "private", mockNamespace(projectGroupId, customerGroupId));
    }

    private Group mockGroup(String name, Integer groupId, Integer parentId) {
        return Group.builder().id(groupId).name(name).path(GitLabPathUtils.generateValidPath(name)).parentId(parentId)
                .build();
    }

    private Project mockProject(Integer id, String name, String visibility, Namespace namespace) {
        return Project.builder().id(id).name(name).visibility(visibility).namespace(namespace).build();
    }

    private Namespace mockNamespace(Integer id, Integer parentId) {
        return Namespace.builder().id(id).parentId(parentId).build();
    }

}
