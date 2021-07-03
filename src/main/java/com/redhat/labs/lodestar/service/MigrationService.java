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
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.EngagementUser;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.Project;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class MigrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationService.class);
    
    private static final String userJson = "users.json";
    
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
    
    @ConfigProperty(name = "migrate.users")
    boolean migrateUsers;
    
    @ConfigProperty(name = "migrate.uuid")
    boolean migrateUuids;
    
    private Map<Integer, Engagement> allEngagements = new HashMap<>();
    
    /**
     * Currently the migration will only occur if config properties are true.
     * The migration is idempotent so no harm in rerunning. It will only update
     * engagements that haven't been migrated. As we get closer to migration time
     * we should evaluate whether this is the right approach.
     * Once the start up is complete this service can not be called.
     * @param ev
     */
    void onStart(@Observes StartupEvent ev) {
        
        if(migrateUsers) {
            LOGGER.info("Migrate users: {}", migrateUsers);
            migrateUsers();
            LOGGER.info("End Migrate users");
        }
        
        if(migrateUuids) {
            LOGGER.info("Migrate uuids: {}", migrateUuids);
            migrateUuids();
            LOGGER.info("End Migrate uuids");
        }  
        
    }
    /**
     * Get all projects and split for individual update
     */
    private void migrateUuids() {
        List<Project> allProjects = projectService.getProjectsByGroup(engagementRepositoryId, true);
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
        }
    }

    /**
     * Get all engagements (engagement.json) and split for individual update
     */
    private void migrateUsers() {     
        getAllEngagements().values().parallelStream().forEach(this::migrateUsersToGitlab);   
    }
    
    /**
     * Get All engagements. This is run by migrate users and uuids so only run once if both are active
     * @return
     */
    private Map<Integer, Engagement> getAllEngagements() {
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
        LOGGER.debug("mcanoy {}",engagement);
        allEngagements.put(engagement.getProjectId(), engagement);
    }
    
    /**
     * This will write the user json to gitlab. Should you wish to rollback or redo you could add this code
     * fileService.deleteFile(engagement.getProjectId(), userJson);
     * @param engagement
     */
    private void migrateUsersToGitlab(Engagement engagement) {
        
        List<EngagementUser> users = engagement.getEngagementUsers();
        
        if(users == null) {
            users = Collections.emptyList();
        }
        
        if(fileService.getFile(engagement.getProjectId(), userJson).isEmpty()) {
            String content = json.toJson(users);
            File file = File.builder().content(content).authorEmail("bot@bot.com").authorName("Jim Bot").branch("master").commitMessage("migrating users").build();
            
            fileService.createFile(engagement.getProjectId(), userJson, file);
            LOGGER.info("Migrated {} users for engagement {}", users.size(), engagement.getUuid());
        }
    }
}
