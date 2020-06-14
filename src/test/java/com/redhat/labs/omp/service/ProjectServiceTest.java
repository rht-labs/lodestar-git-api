package com.redhat.labs.omp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import org.junit.jupiter.api.Test;

import com.redhat.labs.omp.mocks.MockGitLabService;
import com.redhat.labs.omp.models.gitlab.Project;
import com.redhat.labs.omp.models.gitlab.ProjectSearchResults;

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
    
    @Test void getProjectAllByName() {
        List<ProjectSearchResults> found = projectService.getAllProjectsByName("iac");
        assertNotNull(found);
        assertEquals(1, found.size());
        
    }
    
    @Test void getProjectByPath() {
        Optional<Project> found = projectService.getProjectById(66);
        assertFalse(found.isPresent());
    }
    
    @Test void updateProjectValid() {
        Optional<Project> updated = projectService.updateProject(45, new Project());
        assertTrue(updated.isPresent());
    }
    
    @Test void updateProjectInvalid() {
        Optional<Project> updated = projectService.updateProject(46, new Project());
        assertFalse(updated.isPresent());
    }
    
    @Test void deleteProjectWorks() {
        projectService.deleteProject(45);
    }
}
