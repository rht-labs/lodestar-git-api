package com.redhat.labs.lodestar.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.redhat.labs.lodestar.models.*;
import com.redhat.labs.lodestar.models.gitlab.CommitMultiple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.Project;

import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MigrationServiceTest {

    @Inject
    MigrationService migrationService;

    static ProjectService projectServiceMock;
    
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

        List<Category> cats = new ArrayList<>();
        cats.add(Category.builder().uuid("kittycat").name("As")
                .created("2021-08-26T20:42:46.050483").updated("2021-08-26T20:42:46.050483").build());
        cats.add(Category.builder().uuid("meow").name("Snowball").build());

        List<UseCase> uses = new ArrayList<>();
        uses.add(UseCase.builder().title("use case").created("2021-08-26T20:42:46.050483")
                .updated("2021-08-26T20:42:46.050483").build());
        uses.add(UseCase.builder().title("use case2").created("2021-08-26T20:42:46.050483").build());

        Launch launch = Launch.builder().launchedBy("Alfredo").launchedByEmail("a@b.com").launchedDateTime("2021-08-26T20:42:46.050").build();

        Engagement e = Engagement.builder().uuid("a1").categories(cats).useCases(uses).projectId(1).launch(launch).build();
        allEngagements.add(e);

        launch = Launch.builder().launchedBy("Alfredo").launchedByEmail("a@b.com").launchedDateTime(Instant.now().toString()).build();
        e = Engagement.builder().uuid("c3").projectId(3).engagementUsers(engagementUsers).launch(launch).build();
        allEngagements.add(e);

        e = Engagement.builder().uuid("d4").projectId(4).engagementUsers(engagementUsers).artifacts(artifacts).build();
        allEngagements.add(e);

        projectServiceMock = Mockito.mock(ProjectService.class);
        Mockito.when(projectServiceMock.getProjectsByGroup(2, true)).thenReturn(projects);
        QuarkusMock.installMockForType(projectServiceMock, ProjectService.class);
        
        fileServiceMock = Mockito.mock(FileService.class);
        Mockito.when(fileServiceMock.getFile(4, "engagement/participants.json")).thenReturn(Optional.of(File.builder().build()));
        Mockito.when(fileServiceMock.getFile(4, "engagement/hosting.json")).thenReturn(Optional.of(File.builder().build()));
        QuarkusMock.installMockForType(fileServiceMock, FileService.class);

        EngagementService mockES = Mockito.mock(EngagementService.class);
        Mockito.when(mockES.getAllEngagements(Optional.of(false), Optional.of(false))).thenReturn(allEngagements);
        QuarkusMock.installMockForType(mockES, EngagementService.class);
    }
    
    @Test 
    void migrate() {
        migrationService.migrate(false, false, false, false, false,
                false, Collections.emptyList());
        
        Mockito.verify(projectServiceMock, Mockito.never()).updateProject(Mockito.any());
        Mockito.verify(fileServiceMock, Mockito.never()).createFile(Mockito.anyInt(), Mockito.eq("engagement/participants.json"), Mockito.any(File.class));

        migrationService.migrate(true, true, true, true, true, false, Collections.emptyList());

        Mockito.verify(projectServiceMock, Mockito.times(2)).updateProject(Mockito.any());
        Mockito.verify(fileServiceMock, Mockito.times(3)).createFiles(Mockito.anyInt(), Mockito.any(CommitMultiple.class));
        Mockito.verify(fileServiceMock, Mockito.never()).createFile(Mockito.anyInt(), Mockito.eq("engagement/participants.json"), Mockito.any(File.class));
    }
    
    

}
