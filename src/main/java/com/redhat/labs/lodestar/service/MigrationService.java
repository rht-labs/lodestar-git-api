package com.redhat.labs.lodestar.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.labs.lodestar.models.*;
import com.redhat.labs.lodestar.models.gitlab.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.lodestar.config.JsonMarshaller;

@ApplicationScoped
public class MigrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationService.class);

    private static final String ENGAGEMENT_DIR = "engagement/";
    private static final String PARTICIPANT_JSON = ENGAGEMENT_DIR + "participants.json";
    private static final String ARTIFACT_JSON = ENGAGEMENT_DIR + "artifacts.json";
    private static final String HOSTING_JSON = ENGAGEMENT_DIR + "hosting.json";
    private static final String ENGAGEMENT_JSON = ENGAGEMENT_DIR + "engagement.json";
    
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
    
    private final Map<Integer, Engagement> allEngagements = new HashMap<>();
    
    /**
     * The migration is idempotent so no harm in rerunning. It will only update
     * engagements that haven't been migrated. 
     */
    public void migrate(boolean migrateUuids, boolean migrateParticipants, boolean migrateArtifacts, boolean migrateHosting,
                        boolean migrateEngagements, boolean overwrite, List<String> uuids) {
        LOGGER.debug("uuids {} participants {} artifacts {} hosting {} engagements {} overwrite {} uuid {}", migrateUuids,
                migrateParticipants, migrateArtifacts, migrateHosting, migrateEngagements, overwrite, uuids.size());

        getAllEngagements(); //hydrate before stream

        if(migrateUuids) {
            LOGGER.info("Start Migrate uuids");
            migrateUuids();
            LOGGER.info("End Migrate uuids");
        }

        LOGGER.info("Start Migrate content");
        migrateAll(migrateParticipants, migrateArtifacts, migrateHosting, migrateEngagements, overwrite, uuids);
        LOGGER.info("End Migrate content");

    }

    private void migrateAll(boolean migrateParticipants, boolean migrateArtifacts, boolean migrateHosting,
                            boolean migrateEngagements, boolean overwrite, List<String> uuids ) {
        int counter = 0;
        for(Engagement e : getAllEngagements().values()) {

            if(uuids.isEmpty() || uuids.contains(e.getUuid())) {
                counter++;
                LOGGER.debug("Migrating ({}) {}", counter, e.getUuid());
                
                List<Action> actions = new ArrayList<>();
                String content;
                if(migrateEngagements) {
                    content = migrateEngagement(e);
                    actions.add(createAction(content, ENGAGEMENT_JSON, overwrite, e.getProjectId()));
                }

                if(migrateParticipants) {
                    content = migrateParticipantsToGitlab(e);
                    actions.add(createAction(content, PARTICIPANT_JSON, overwrite, e.getProjectId()));
                }

                if(migrateHosting) {
                    content = migrateHostingToGitlab(e);
                    actions.add(createAction(content, HOSTING_JSON, overwrite, e.getProjectId()));
                }

                if(migrateArtifacts) {
                    content = migrateArtifactsToGitlab(e);
                    actions.add(createAction(content, ARTIFACT_JSON, overwrite, e.getProjectId()));
                }

                if(!actions.isEmpty()) {
                    String commitMessage = "Migrating to v2";

                    CommitMultiple commit = CommitMultiple.builder().id(e.getProjectId()).branch(commitBranch).commitMessage(commitMessage).actions(actions)
                            .authorName(commitAuthor).authorEmail(commitEmail).build();

                    fileService.createFiles(e.getProjectId(), commit);
                }
            }
        }
    }

    /**
     *
     * @param content the new content to write
     * @param filePath the file path to write to
     * @param overwrite if true - will check for existence of file and overwrite if it exists
     * @return the file to write to on commit
     */
    private Action createAction(String content, String filePath, boolean overwrite, int projectId) {
        FileAction action = FileAction.create;
        if(overwrite && fileService.getFile(projectId, filePath).isPresent()) {
            action = FileAction.update;

        }
        return Action.builder().action(action).filePath(filePath).content(content).encoding("base64").build();
    }
    
    /**
     * Get all projects and split for individual update. This will add a description to any 
     * project that doesn't have one. The description will include the uuid of the engagement
     */
    private void migrateUuids() {
        List<Project> allProjects = projectService.getProjectsByGroup(engagementRepositoryId, true);

        allProjects.parallelStream().forEach(this::updateProjectWithUuid);
    }

    private String migrateEngagement(Engagement engagement) {
        Engagement copy = clone(engagement, Engagement.class);

        if(copy.getCategories() != null) {
            copy.getCategories().forEach(cat -> {
                try {
                    cat.setCreated(convertDateTime(cat.getCreated()));
                    cat.setUpdated(convertDateTime(cat.getUpdated()));
                } catch(Exception ex) {
                    LOGGER.error ("dtf exception {}", cat.getCreated(), ex);
                }
            });
        }

        if(copy.getUseCases() != null) {
            copy.getUseCases().forEach((use -> {
                try {
                    use.setCreated(convertDateTime(use.getCreated()));
                    use.setUpdated(convertDateTime(use.getUpdated()));
                } catch(Exception ex) {
                    LOGGER.error ("dtf exception {}", use.getCreated(), ex);
                }
            }));
        }

        copy.setHostingEnvironments(null);
        copy.setEngagementUsers(null);
        copy.setCommits(null);
        copy.setArtifacts(null);

        return json.toJson(copy);
    }

    private <T> T clone(T toClone, Class<T> clazz) {
        return json.fromJson(json.toJson(toClone), clazz);
    }

    private String convertDateTime(String oldDateTime) {
        LOGGER.trace("Date In {}", oldDateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime expected = LocalDateTime.parse(oldDateTime, formatter);
        Instant instant = expected.toInstant(ZoneOffset.UTC);
        LOGGER.trace("Date Out {}", instant);
        return instant.toString();
    }
    
    /**
     * Update a single project if the description is not already set and an engagement has been found
     * @param project the project to update
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
    
    private String migrateArtifactsToGitlab(Engagement engagement) {
        List<Artifact> artifacts = engagement.getArtifacts() == null ? Collections.emptyList() : engagement.getArtifacts();
        List<Artifact> copies = new ArrayList<>(artifacts.size());
        artifacts.forEach(a -> {
            Artifact copy = clone(a, Artifact.class);
            copy.setRegion(copy.getRegion());
            if(copy.getCreated() == null) {
                copy.setCreated(engagement.getEndDate());
            }

            if(copy.getUpdated() == null) {
                copy.setUpdated(engagement.getEndDate());
            }
            copies.add(copy);
        });
        return json.toJson(copies);
    }
    
    /**
     * Get All engagements. This is run by migrate users and uuids so only run once if both are active
     * @return a map of engagements keyed by project id
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
     * @param engagement map this
     */
    private void addToMap(Engagement engagement) {
        allEngagements.put(engagement.getProjectId(), engagement);
    }
    
    /**
     * This will write the user json to gitlab. Should you wish to rollback or redo you could add this code
     * fileService.deleteFile(engagement.getProjectId(), userJson);
     * @param engagement participants of this engagement
     */
    private String migrateParticipantsToGitlab(Engagement engagement) {
        
        List<EngagementUser> participants = engagement.getEngagementUsers() == null ? Collections.emptyList() : engagement.getEngagementUsers();
        participants.forEach(p -> p.setRegion(engagement.getRegion()));
        return json.toJson(participants);
    }
    
    private String migrateHostingToGitlab(Engagement engagement) {
        List<HostingEnvironment> hosting = engagement.getHostingEnvironments() == null ? Collections.emptyList() : engagement.getHostingEnvironments();
        hosting.forEach(h -> h.setRegion(engagement.getRegion()));
        return json.toJson(hosting);
    }

}
