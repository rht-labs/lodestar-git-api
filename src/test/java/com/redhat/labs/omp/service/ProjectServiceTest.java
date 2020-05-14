package com.redhat.labs.omp.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.redhat.labs.omp.models.gitlab.Project;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ProjectServiceTest {

    @Inject
    ProjectService projectService;
    
    @Test
    public void createValidProject() {
        
        Project project = Project.builder().name("valid").build();
        Optional<Project> created = projectService.createProject(project);
        
        Assertions.assertTrue(created.isPresent());
    }
    
    @Test
    public void createInValidProject() {
        
        Project project = Project.builder().name("invalid").build();
        Optional<Project> created = projectService.createProject(project);
        
        Assertions.assertFalse(created.isPresent());
    }
    
    @Test 
    public void getProjectsByGroup() {
        List<Project> projects = projectService.getProjectsByGroup(10, true);
        
        Assertions.assertEquals(1, projects.size());
    }
}
