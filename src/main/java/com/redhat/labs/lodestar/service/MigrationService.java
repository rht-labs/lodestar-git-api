package com.redhat.labs.lodestar.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.lodestar.config.JsonMarshaller;
import com.redhat.labs.lodestar.models.Artifact;
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.EngagementUser;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.Project;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class MigrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationService.class);
    
    private static final String PARTICIPANT_JSON = "participants.json";
    private static final String ARTIFACT_JSON = "artifacts.json";
    
    @Inject
    EngagementService engagementService;

    @Inject
    FileService fileService;
    
    @Inject 
    ProjectService projectService;

    @Inject
    JsonMarshaller json;
    
    @ConfigProperty(name = "engagements.repository.id")
    int engagementRepositoryId;
    
    @ConfigProperty(name = "commit.default.email")
    String commitEmail;
    
    @ConfigProperty(name = "commit.default.author")
    String commitAuthor;
    
    @ConfigProperty(name = "commit.default.branch")
    String commitBranch;
    
    private Map<Integer, Engagement> allEngagements = new HashMap<>();
    
    /**
     * The migration is idempotent so no harm in rerunning. It will only update
     * engagements that haven't been migrated. 
     */
    public void migrate(boolean migrateUuids, boolean migrateParticipants, boolean migrateArtifacts) {
        if(migrateUuids) {
            LOGGER.info("Start Migrate uuids: {}", migrateUuids);
            migrateUuids();
            LOGGER.info("End Migrate uuids");
        } 
        
        if(migrateParticipants) {
            LOGGER.info("Start Migrate participants: {}", migrateParticipants);
            migrateParticipants();
            LOGGER.info("End Migrate participants");
        } 
        
        if(migrateArtifacts) {
            LOGGER.info("Start Migrate artifacts");
            migrateArtifacts();
            LOGGER.info("End Migrate artifacts");
        }
    }
    
    /**
     * Get all projects and split for individual update. This will add a description to any 
     * project that doesn't have one. The description will included the uuid of the engagement
     */
    private void migrateUuids() {
        List<Project> allProjects = projectService.getProjectsByGroup(engagementRepositoryId, true);
        getAllEngagements(); //hydrate before stream
        allProjects.parallelStream().forEach(this::updateProjectWithUuid);
    }
    
    /**
     * Update a single project if the description is not already set and an engagement has been found
     * @param project
     */
    private void updateProjectWithUuid(Project project) {
        
        if(project.getDescription() == null && getAllEngagements().containsKey(project.getId())) {
            String uuid = allEngagements.get(project.getId()).getUuid();
            project.setDescription(String.format(ProjectStructureService.ENGAGEMENT_PROJECT_DESCRIPTION, uuid));
            projectService.updateProject(project);
            
            LOGGER.info("Added uuid {} to project {} {}", uuid, project.getId(), project);
        } else {
            LOGGER.info("Skipped uuid update because description is already set or the project {} is not in the engagement map", project.getId());
        }
    }

    /**
     * Get all engagements (engagement.json) and split for individual update
     */
    private void migrateParticipants() {     
        getAllEngagements().values().parallelStream().forEach(this::migrateParticipantsToGitlab);   
    }
    
    private void migrateArtifacts() {
        getAllEngagements().values().parallelStream().forEach(this::migrateArtifactsToGitlab);
    }
    
    private void migrateArtifactsToGitlab(Engagement engagement) {
        List<Artifact> artifacts = engagement.getArtifacts() == null ? Collections.emptyList() : engagement.getArtifacts();
        String content = json.toJson(engagement.getArtifacts());
        migrateToGitlab(engagement, content, ARTIFACT_JSON, artifacts.size());
        
    }
    
    /**
     * Get All engagements. This is run by migrate users and uuids so only run once if both are active
     * @return
     */
    private Map<Integer, Engagement> getAllEngagements() {
        LOGGER.debug("Engagement count (pre-fetch) {}", allEngagements.size());
        if (allEngagements.isEmpty()) {
            List<Engagement> engagements = engagementService.getAllEngagements(Optional.of(false), Optional.of(false));
            engagements.parallelStream().forEach(this::addToMap);
            LOGGER.debug("fetched engagements for migration {} ", allEngagements.size());
        }
        
        return allEngagements;
    }
    
    /**
     * Do a quick project id to engagement mapping to easily find the engagement by project id
     * @param engagement
     */
    private void addToMap(Engagement engagement) {
        allEngagements.put(engagement.getProjectId(), engagement);
    }
    
    /**
     * This will write the user json to gitlab. Should you wish to rollback or redo you could add this code
     * fileService.deleteFile(engagement.getProjectId(), userJson);
     * @param engagement
     */
    private void migrateParticipantsToGitlab(Engagement engagement) {
        
        List<EngagementUser> participants = engagement.getEngagementUsers() == null ? Collections.emptyList() : engagement.getEngagementUsers();
        String content = json.toJson(participants);
        migrateToGitlab(engagement, content, PARTICIPANT_JSON, participants.size());
    }
    
    /**
     * This will write the user json to gitlab. Should you wish to rollback or redo you could add this code
     * fileService.deleteFile(engagement.getProjectId(), userJson);
     * @param engagement
     */
    private void migrateToGitlab(Engagement engagement, String content, String fileName, int size) {
        
        if(fileService.getFile(engagement.getProjectId(), fileName).isEmpty()) {
            File file = File.builder().content(content).authorEmail(commitEmail).authorName(commitAuthor).branch(commitBranch).commitMessage(String.format("migrating %s", fileName)).build();
            fileService.createFile(engagement.getProjectId(), fileName, file);
            LOGGER.info("Migrated {} {} for engagement {}", size, fileName, engagement.getUuid());
        }
    }
}
