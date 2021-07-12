package com.redhat.labs.lodestar.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.redhat.labs.lodestar.models.Artifact;
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.EngagementUser;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.Project;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MigrationServiceTest {

    @Inject
    MigrationService migrationService;

    static ProjectService mockPS;
    
    static FileService fileServiceMock;

    @BeforeAll
    public static void setup() {

        Project p1 = Project.builder().name("iac").id(11).build();
        Project p2 = Project.builder().name("iac").description("Hola").id(1).build();
        Project p3 = Project.builder().name("iac").id(3).build();
        Project p4 = Project.builder().name("iac").id(4).build();

        List<Project> projects = new ArrayList<>();
        projects.add(p1); // id doesn't match
        projects.add(p2); // already has description
        projects.add(p3); // id good, no description
        projects.add(p4); // id good, no description

        List<EngagementUser> engagementUsers = new ArrayList<>();
        engagementUsers.add(EngagementUser.builder().uuid("b2").build());
        
        List<Artifact> artifacts = new ArrayList<>();
        artifacts.add(Artifact.builder().uuid("1").build());

        List<Engagement> allEngagements = new ArrayList<>();

        Engagement e = Engagement.builder().uuid("a1").projectId(1).build();
        allEngagements.add(e);

        e = Engagement.builder().uuid("c3").projectId(3).engagementUsers(engagementUsers).build();
        allEngagements.add(e);

        e = Engagement.builder().uuid("d4").projectId(4).engagementUsers(engagementUsers).artifacts(artifacts).build();
        allEngagements.add(e);

        mockPS = Mockito.mock(ProjectService.class);
        Mockito.when(mockPS.getProjectsByGroup(2, true)).thenReturn(projects);
        QuarkusMock.installMockForType(mockPS, ProjectService.class);
        
        fileServiceMock = Mockito.mock(FileService.class);
        Mockito.when(fileServiceMock.getFile(4, "participants.json")).thenReturn(Optional.of(File.builder().build()));
        QuarkusMock.installMockForType(fileServiceMock, FileService.class);

        EngagementService mockES = Mockito.mock(EngagementService.class);
        Mockito.when(mockES.getAllEngagements(Optional.of(false), Optional.of(false))).thenReturn(allEngagements);
        QuarkusMock.installMockForType(mockES, EngagementService.class);
    }
    
    @Test 
    void migrate() {
        migrationService.migrate(false, false, false);
        
        Mockito.verify(mockPS, Mockito.never()).updateProject(Mockito.any());
        Mockito.verify(fileServiceMock, Mockito.never()).createFile(Mockito.anyInt(), Mockito.eq("participants.json"), Mockito.any(File.class));

        migrationService.migrate(true, true, true);

        Mockito.verify(mockPS, Mockito.times(2)).updateProject(Mockito.any());
        Mockito.verify(fileServiceMock, Mockito.times(2)).createFile(Mockito.anyInt(), Mockito.eq("participants.json"), Mockito.any(File.class));
    }
    
    

}
