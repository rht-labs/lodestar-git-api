package com.redhat.labs.lodestar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.redhat.labs.lodestar.mocks.MockGitLabService;
import com.redhat.labs.lodestar.models.gitlab.Commit;
import com.redhat.labs.lodestar.models.gitlab.Project;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ProjectServiceTest {

    @Inject
    ProjectService projectService;
    
    @Test void createValidProject() {
        
        Project project = Project.builder().name("valid").build();
        Optional<Project> created = projectService.createProject(project);
        
        assertTrue(created.isPresent());
    }
    
    @Test void createInValidProject() {
        
        Project project = Project.builder().name("invalid").build();
        Optional<Project> created = projectService.createProject(project);
        
        assertFalse(created.isPresent());
    }
    
    @Test void createAndPreserve() {
        ProjectService noquarkus = new ProjectService();
        noquarkus.doNotDelete = true;
        noquarkus.gitLabService = new MockGitLabService();
        
        Optional<Project> project = noquarkus.createProject(Project.builder().build());
        
        assertTrue(project.isPresent());
        Project p = project.get();
        assertNotNull(p.getTagList());
        assertEquals(1, p.getTagList().size());
        assertEquals("DO_NOT_DELETE", p.getTagList().get(0));
    }
    
    @Test void getProjectsByGroup() {
        List<Project> projects = projectService.getProjectsByGroup(10, true);
        
        assertEquals(1, projects.size());
    }
    
    @Test void getProjectFound() {
        Optional<Project> found = projectService.getProjectByName(45, "iac");
        
        assertTrue(found.isPresent());
    }
    
    @Test void getProjectFoundButWrongNamespace() {
        Optional<Project> found = projectService.getProjectByName(46, "iac");
        
        assertFalse(found.isPresent());
    }
    
    @Test void getProjectNotFound() {
        Optional<Project> notFound = projectService.getProjectByName(77, "Banana");
        
        assertEquals(Optional.empty(), notFound);
    }
    
    @Test void getProjectByPath() {
        Optional<Project> found = projectService.getProjectById(66);
        assertFalse(found.isPresent());
    }
    
    @Test void updateProjectValid() {
        Project p = Project.builder().id(45).build();
        Optional<Project> updated = projectService.updateProject(p);
        assertTrue(updated.isPresent());
    }
    
    @Test void updateProjectInvalid() {
        Project p = Project.builder().id(46).build();
        Optional<Project> updated = projectService.updateProject(p);
        assertFalse(updated.isPresent());
    }
    
    @Test void deleteProjectWorks() {
        projectService.deleteProject(45);
        assertNotNull(projectService);
    }

    @ParameterizedTest
    @CsvSource({"multi/page/iac,6", "multi/page/missingheader,3", "multi/page/filtered,4"})
    void getCommitLog(String project, Integer expectedCommitSize) {

        List<Commit> commits = projectService.getCommitLog(project);
        assertNotNull(commits);
        assertEquals(expectedCommitSize, commits.size());

    }
    
    @Test void getProjectByEngagementUuid() {
        Optional<Project> p = projectService.getProjectByEngagementUuid(1, "a");
        
        assertFalse(p.isEmpty());
        assertEquals(1, p.get().getId());
    }
    
    @Test void getProjectByEngagementUuidNotFound() {
        Optional<Project> p = projectService.getProjectByEngagementUuid(7, "a");
        
        assertTrue(p.isEmpty());
    }
    
    @Test void getProjectByEngagementUuidNotFoundException() {
        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            projectService.getProjectByEngagementUuid(8, "a");
        });
        
        assertEquals(500, ex.getResponse().getStatus());
    }

}
