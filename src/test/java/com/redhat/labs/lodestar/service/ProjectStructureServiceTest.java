package com.redhat.labs.lodestar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

//    @Test
//    void testThis() {
//
//        String customerName = "customer1";
//        String projectName = "project1";
//
//        Group cGroup = Group.builder().name(customerName).path(GitLabPathUtils.generateValidPath(customerName))
//                .parentId(null).build();
//
//        Group pGroup = Group.builder().name(projectName).path(GitLabPathUtils.generateValidPath(projectName))
//                .parentId(null).build();
//
////        when(groupService.createGitLabGroup(ArgumentMatchers.argThat((g) -> g.getName().equals(customerName)))).thenReturn(Optional.of(cGroup));
////        when(groupService.createGitLabGroup(ArgumentMatchers.argThat((g) -> g.getName().equals(customerName)))).thenReturn(Optional.of(pGroup));
//        when(groupService.createGitLabGroup(Mockito.any(Group.class)))
//                .thenAnswer((i) -> Optional.of(i.getArgument(0, Group.class)));
//
//        Optional<Group> group = psService.testThis(customerName);
//        System.out.print(group.orElse(null));
//
//    }

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
