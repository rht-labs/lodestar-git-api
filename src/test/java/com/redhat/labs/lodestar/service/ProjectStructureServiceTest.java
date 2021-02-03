package com.redhat.labs.lodestar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Namespace;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.rest.client.GitLabService;
import com.redhat.labs.lodestar.utils.GitLabPathUtils;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
class ProjectStructureServiceTest {

    @Inject
    ProjectStructureService psService;

    @InjectMock
    @RestClient
    GitLabService gitLabService;

    // New Customers and Projects

    @Test
    void testCreateProjectStructureForNewCustomerAndNewProject() {

        // given
        String customerName = "customer1";
        String projectName = "project1";

        Group cGroup = Group.builder().name(customerName).path(GitLabPathUtils.generateValidPath(customerName)).id(2222)
                .parentId(1111).build();
        Group pGroup = Group.builder().name(projectName).path(GitLabPathUtils.generateValidPath(projectName))
                .parentId(2222).id(3333).build();
        Project project = Project.builder().id(4444).name("iac").visibility("private")
                .namespace(Namespace.builder().id(3333).parentId(2222).build()).build();

        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(null);
        given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.any(Integer.class), Mockito.any(Integer.class)))
                .willReturn(Response.ok(Arrays.asList()).header("X-Total-Pages", 1).build());
        given(gitLabService.createGroup(Mockito.any(Group.class))).willReturn(cGroup, pGroup);
        given(gitLabService.createProject(Mockito.any(Project.class))).willReturn(project);

        Engagement engagement = Engagement.builder().customerName(customerName).projectName(projectName).build();

        // when
        Project actual = psService.createOrUpdateProjectStructure(engagement, "http://some-path/engagements");

        // then
        assertNotNull(project);
        assertEquals("iac", actual.getName());
        assertEquals("private", actual.getVisibility());
        assertEquals(3333, actual.getNamespace().getId());
        assertEquals(2222, actual.getNamespace().getParentId());

    }

    @Test
    void testCreateProjectStructureForCustomerNameChange() {

        // given
        String customerName = "customer1";
        String projectName = "project1";

        Group cGroup = Group.builder().name(customerName).path(GitLabPathUtils.generateValidPath(customerName)).id(2222)
                .parentId(2).build();
        Group pGroup = Group.builder().name(projectName).path(GitLabPathUtils.generateValidPath(projectName))
                .parentId(2222).id(3333).build();
        Project project = Project.builder().id(4444).name("iac").visibility("private")
                .namespace(Namespace.builder().id(3333).parentId(2222).build()).build();
        Group newCustomerGroup = Group.builder().name("newCustomer1")
                .path(GitLabPathUtils.generateValidPath("newCustomer1")).id(2222).parentId(2).build();

        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(project);
        given(gitLabService.getGroupByIdOrPath(Mockito.anyString())).willReturn(pGroup, cGroup);
        given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.eq(100), Mockito.eq(1)))
                .willReturn(Response.ok(Lists.newArrayList()).header("X-Total-Pages", 1).build());
        given(gitLabService.updateGroup(Mockito.anyInt(), Mockito.any())).willReturn(newCustomerGroup);

        Engagement engagement = Engagement.builder().customerName("newCustomer1").projectName(projectName).build();

        // when
        Project actual = psService.createOrUpdateProjectStructure(engagement, "http://some-path/engagements");

        // then
        assertNotNull(project);
        assertEquals("iac", actual.getName());
        assertEquals("private", actual.getVisibility());
        assertEquals(3333, actual.getNamespace().getId());
        assertEquals(2222, actual.getNamespace().getParentId());

    }

    @Test
    void testCreateProjectStructureForCustomerNameChangeCustomerHasMultipleProjects() {

        // given
        String customerName = "customer1";
        String projectName = "project1";

        Group cGroup = Group.builder().name(customerName).path(GitLabPathUtils.generateValidPath(customerName)).id(2222)
                .parentId(2).build();
        Group pGroup = Group.builder().name(projectName).path(GitLabPathUtils.generateValidPath(projectName))
                .parentId(2222).id(3333).build();
        Group anotherGroup = Group.builder().name(projectName).path(GitLabPathUtils.generateValidPath("anotherGroup"))
                .parentId(2222).id(5555).build();
        Project project = Project.builder().id(4444).name("iac").visibility("private")
                .namespace(Namespace.builder().id(3333).parentId(2222).build()).build();
        Group newCustomerGroup = Group.builder().name("newCustomer1")
                .path(GitLabPathUtils.generateValidPath("newCustomer1")).id(6666).parentId(2).build();

        given(gitLabService.getProjectById(Mockito.anyString())).willReturn(project);
        given(gitLabService.getGroupByIdOrPath(Mockito.anyString())).willReturn(pGroup, cGroup);
        given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.eq(100), Mockito.eq(1)))
                .willReturn(Response.ok(Lists.newArrayList(pGroup, anotherGroup)).header("X-Total-Pages", 1).build());
        given(gitLabService.updateGroup(Mockito.anyInt(), Mockito.any())).willReturn(newCustomerGroup);
        given(gitLabService.createGroup(Mockito.any(Group.class))).willReturn(newCustomerGroup);
        given(gitLabService.transferProject(Mockito.anyInt(), Mockito.any())).willReturn(Optional.of(project));

        Engagement engagement = Engagement.builder().customerName("newCustomer1").projectName(projectName).build();

        // when
        Project actual = psService.createOrUpdateProjectStructure(engagement, "http://some-path/engagements");

        // then
        assertNotNull(project);
        assertEquals("iac", actual.getName());
        assertEquals("private", actual.getVisibility());

    }

}

//New Customers and Projects - 2/2
//--------------------------
//Create new customer and project - X
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//Create new project for existing customer - X
//-  reuse customer group (c1), create new project group (p2), create new project (iac)
//
//Rename Projects 1/2
//---------------
//Rename Project - New Name NOT Taken - DONE
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  rename project
//-  RESULT: customer group stays the same, project group renamed, project stays the same
//Rename Project - New Name Taken - TODO  --> returns a 
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  rename project
//-  RESULT: exception???
//
//Rename Customer and/or Project to New Customer (no name conflicts) 2/4
//------------------------------------------------------------------
//Rename Customer (only 1 project) to New Customer
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  rename customer
//-  RESULT: customer group renamed, new project group created, project moved to new project group, old customer/project groups removed
//Rename Customer (2 projects) to New Customer - DONE
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  create new customer group (c1), create new project group (p2), create new project (iac)
//-  rename customer
//-  RESULT: customer group renamed, new project group created, project moved to new project group, old project group removed, old customer group remains
//Rename Customer (only 1 project) and Project to New Customer 
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  rename customer and project
//-  RESULT: customer group renamed, new project group created, project moved to new project group, old customer/project groups removed
//Rename Customer (2 projects) and Project to New Customer - DONE
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  create new customer group (c1), create new project group (p2), create new project (iac)
//-  rename customer and project
//-  RESULT: customer group renamed, new project group created, project moved to new project group, old project group removed, old customer group remains
//
//
//Rename Customer and/or Project to Existing Customer (no name conflicts) 4/4
//-----------------------------------------------------------------------
//Rename Customer (only 1 project) to Existing Customer - DONE
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  create new customer group (see1), create new project group (p1), create new project (iac)
//-  rename customer
//-  RESULT: existing customer group used, new project group created, project moved to new project group, old customer/project groups removed
//Rename Customer (2 projects) to Existing Customer - DONE
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  create new customer group (c1), create new project group (p2), create new project (iac)
//-  create new customer group (see1), create new project group (p3), create new project (iac)
//-  rename customer
//-  RESULT: existing customer group used, new project group created, project moved to new project group, old project group removed, old customer group remains
//Rename Customer (only 1 project) and Project to Existing Customer - DONE
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  create new customer group (see1), create new project group (p2), create new project (iac)
//-  rename customer and project
//-  RESULT: existing customer group used, new project group created, project moved to new project group, old customer/project groups removed
//Rename Customer (2 projects) and Project to Existing Customer - DONE
//-  create new customer group (c1), create new project group (p1), create new project (iac)
//-  create new customer group (c1), create new project group (p2), create new project (iac)
//-  create new customer group (see1), create new project group (p3), create new project (iac)
//-  rename customer and project
//-  RESULT: existing customer, new project group created, project moved to new project group, old project group removed, old customer group remains
//
