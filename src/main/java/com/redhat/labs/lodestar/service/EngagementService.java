package com.redhat.labs.lodestar.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.lodestar.config.JsonMarshaller;
import com.redhat.labs.lodestar.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.Status;
import com.redhat.labs.lodestar.models.gitlab.Action;
import com.redhat.labs.lodestar.models.gitlab.Commit;
import com.redhat.labs.lodestar.models.gitlab.CommitMultiple;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.FileAction;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.HookConfig;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.utils.GitLabPathUtils;

@ApplicationScoped
public class EngagementService {
    public static final Logger LOGGER = LoggerFactory.getLogger(EngagementService.class);

    private static final String ENGAGEMENT_PROJECT_NAME = "iac";
    private static final String DEFAULT_BRANCH = "master";
    private static final String ENGAGEMENT_FILE = "engagement.json";
    private static final String STATUS_FILE = "status.json";
    
    private String engagementPathPrefix;

    @ConfigProperty(name = "engagements.repository.id")
    Integer engagementRepositoryId;

    @ConfigProperty(name = "stripPathPrefix", defaultValue = "schema/")
    String stripPathPrefix;

    @ConfigProperty(name = "gitlab.deploy.key")
    Integer deployKey;

    @Inject
    ProjectService projectService;

    @Inject
    GroupService groupService;

    @Inject
    FileService fileService;
    
    @Inject
    HookService hookService;

    @Inject
    JsonMarshaller json;
    
    @Inject
    ConfigService configService;
    
    @PostConstruct
    public void setPathPrefix() {
        Optional<Group> groupOption = groupService.getGitLabGroupByById(engagementRepositoryId);
 
        if(groupOption.isPresent()) {
            engagementPathPrefix = groupOption.get().getFullPath();
            LOGGER.info("Engagement repo set- to {}", engagementPathPrefix);
        } else {
            LOGGER.warn("Could not find the path for repo {}", engagementRepositoryId);
        }
    }

    // create an engagement
    public Project createEngagement(Engagement engagement, String author, String authorEmail, 
            Optional<String> previousCustomerName, Optional<String> previousProjectName) {

        // create project structure
        Project project = createProjectStucture(engagement, previousCustomerName, previousProjectName);
        engagement.setProjectId(project.getId());

        // get commit message before creating file
        Optional<String> commitMessageOptional = Optional.ofNullable(engagement.getCommitMessage());

        // get all template files
        List<File> repoFiles = new ArrayList<>();
        repoFiles.add(createEngagmentFile(engagement));

        // create actions for multiple commit
        CommitMultiple commit = createCommitMultiple(repoFiles, project.getId(), DEFAULT_BRANCH, author,
                authorEmail, project.isFirst(), commitMessageOptional);

        if (LOGGER.isDebugEnabled()) {
            commit.getActions().stream().forEach(file -> LOGGER.debug("Action File path :: {}", file.getFilePath()));
        }

        // send commit to gitlab
        if (!fileService.createFiles(project.getId(), commit)) {
            throw new UnexpectedGitLabResponseException("failed to commit files for engagement creation.");
        }
        
        List<HookConfig> hookConfigs = configService.getHookConfig();
        hookConfigs.stream().forEach(hookC -> {
            Hook hook = Hook.builder().projectId(engagement.getProjectId()).pushEvents(true)
                    .url(hookC.getBaseUrl()).token(hookC.getToken()).build();
            if(project.isFirst()) { //No need to check for existing hooks first time
                hookService.createProjectHook(engagement.getProjectId(), hook);
            } else {
                hookService.createOrUpdateProjectHook(engagement.getProjectId(), hook);
            }
        });

        return project;

    }
    
    public List<Commit> getCommitLog(String customerName, String engagementName) {
        String projectPath = getPath(customerName, engagementName);
        return projectService.getCommitLog(projectPath);
    }
    
    public List<Hook> getHooks(String customer, String engagment) {
        Optional<Project> project = getProject(customer, engagment);
        
        if(project.isPresent()) {
            return hookService.getProjectHooks(project.get().getId());
        }
        
        return new ArrayList<>();
    }
    
    public Response createHook(String customerName, String engagementName, Hook hook) {
        Response created = Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity("project doesn't exist").build();
        Optional<Project> optional = getProject(customerName, engagementName);
        
        if(optional.isPresent()) {
            List<Hook> hooks = hookService.getProjectHooks(optional.get().getId());
            boolean hookExists = hooks.stream().anyMatch(h -> h.getUrl().equals(hook.getUrl()));
            if(!hookExists) {
                created = hookService.createProjectHook(optional.get().getId(), hook);
            }
        }
        
        return created;
    }
    
    public Status getProjectStatus(String customerName, String engagementName) {
        Status status = null;
        Optional<File> file = fileService.getFile(this.getPath(customerName, engagementName), STATUS_FILE);
        if(file.isPresent()) {
            status = json.fromJson(file.get().getContent(), Status.class);
        }
        
        return status;
    }
    
    public Optional<Project> getProject(String customerName, String engagementName) {
        String fullPath = this.getPath(customerName, engagementName);
        
        LOGGER.debug("Full path {}", fullPath);
        return projectService.getProjectByIdOrPath(fullPath);
    }
    

    /**
     * Gets all engagements from the base group Structure is BaseGroup - customer
     * group - engagement group - project (repo) - engagement file This is search
     * for all projects named 'iac' that our bot has access to. Then looking for the
     * config data
     * 
     * @return A list or engagements
     */
    public List<Engagement> getAllEngagements() {

        List<Project> projects = projectService.getProjectsByGroup(engagementRepositoryId, true);

        return
            projects
                .parallelStream()
                .map(project -> {
                    return getEngagement(project, true);
                })
                .filter(optional -> optional.isPresent())
                .map(optional -> {
                    return optional.get();
                })
                .collect(Collectors.toList());

    }

    public Engagement getEngagement(String namespaceOrId, boolean includeStatus) {
        Engagement engagement = null;

        Optional<Project> project = projectService.getProjectByIdOrPath(namespaceOrId);

        if(project.isPresent()) {
            engagement = getEngagement(project.get(), includeStatus).orElse(null);
        }

        return engagement;
    }
    
    public Engagement getEngagement(String customerName, String engagementName, boolean includeStatus) {
        Engagement engagement = null;
        
        Optional<Project> project = getProject(customerName, engagementName);
        
        if(project.isPresent()) {
            engagement = getEngagement(project.get(), includeStatus).orElse(null);
        }
        
        return engagement;
    }
    
    private Optional<Engagement> getEngagement(Project project, boolean includeStatus) {
        Engagement engagement = null;
        
        Optional<File> engagementFile = fileService.getFileAllow404(project.getId(), ENGAGEMENT_FILE);
        if (engagementFile.isPresent()) {
            engagement = json.fromJson(engagementFile.get().getContent(), Engagement.class);
            
            List<Commit> commits = projectService.getCommitLog(String.valueOf(engagement.getProjectId()));
            engagement.setCommits(commits);
        }
        
        if(includeStatus && engagement != null) {
            Optional<File> statusFile = fileService.getFileAllow404(project.getId(), STATUS_FILE);
            if(statusFile.isPresent()) {
                engagement.setStatus(json.fromJson(statusFile.get().getContent(), Status.class));
            }
        }
        
        return Optional.ofNullable(engagement);
    }

    private File createEngagmentFile(Engagement engagement) {

        //Git api is read only here.
        engagement.setCommits(null);
        engagement.setStatus(null);
        engagement.setCommitMessage(null);

        String fileContent = json.toJson(engagement);
        return File.builder().content(fileContent).filePath(ENGAGEMENT_FILE).build();
    }

    private Project createProjectStucture(Engagement engagement, Optional<String> previousCustomerName,
            Optional<String> previousProjectName) {

        // create group for customer name
        Group customerGroup = getCreateOrUpdateGroup(
                Group.builder().name(engagement.getCustomerName())
                        .path(GitLabPathUtils.generateValidPath(engagement.getCustomerName()))
                        .parentId(engagementRepositoryId).build(),
                        previousCustomerName);

        // create group for project name
        Group projectGroup = getCreateOrUpdateGroup(
                Group.builder().name(engagement.getProjectName())
                        .path(GitLabPathUtils.generateValidPath(engagement.getProjectName()))
                        .parentId(customerGroup.getId()).build(),
                        previousProjectName);

        // create project under project name group
        Project project = getOrCreateProject(projectGroup.getId(), ENGAGEMENT_PROJECT_NAME, Project.builder()
                .name(ENGAGEMENT_PROJECT_NAME).visibility("private").namespaceId(projectGroup.getId()).build());

        // enable deployment key on project
        projectService.enableDeploymentKeyOnProject(project.getId(), deployKey);

        return project;

    }

    private Group getOrCreateGroup(Group groupToCreate) {

        Optional<Group> optional = groupService.getGitLabGroupByName(groupToCreate.getName(), groupToCreate.getParentId());

        if (!optional.isPresent()) {

            // try to create group
            optional = groupService.createGitLabGroup(groupToCreate);

            if (!optional.isPresent()) {
                throw new UnexpectedGitLabResponseException("failed to create group");
            }

        }

        return optional.get();

    }

    private Group updateGroupNameAndPath(String newName, String previousName, Integer parentId) {

        // get existing group
        Optional<Group> optional = groupService.getGitLabGroupByName(previousName, parentId);
        Group existing = optional.orElseThrow(() -> 
            new WebApplicationException("failed to find group with name '" + previousName + "'", 404));

        existing.setName(newName);
        existing.setPath(GitLabPathUtils.generateValidPath(newName));

        optional = groupService.updateGitLabGroup(existing.getId(), existing);
        return optional.orElseThrow(() -> 
            new WebApplicationException("failed to update group name '" + newName + 
                    "' and path '" + existing.getPath() + "'", 500));

    }

    private Group getCreateOrUpdateGroup(Group group, Optional<String> previousGroupName) {

        // get or create group if group name not changed
        if (previousGroupName.isEmpty()) {
            return getOrCreateGroup(group);
        }

        // update name/path of group if changed
        return updateGroupNameAndPath(group.getName(), previousGroupName.get(), group.getParentId());

    }

    private Project getOrCreateProject(Integer namespaceId, String projectName, Project project) {

        Optional<Project> optional = projectService.getProjectByName(namespaceId, projectName);

        if (!optional.isPresent()) {

            // try to create project
            optional = projectService.createProject(project);

            if (!optional.isPresent()) {
                throw new UnexpectedGitLabResponseException("failed to create project");
            }

            optional.get().setFirst(true);
        }

        return optional.get();

    }

    private CommitMultiple createCommitMultiple(List<File> filesToCommit, Integer projectId, String branch,
            String authorName, String authorEmail, boolean isNew, Optional<String> commitMessageOptional) {

        List<Action> actions = new ArrayList<>();

        // convert each file to action - parallelStream was bringing inconsistent
        // results
        filesToCommit.stream().forEach(file -> actions.add(createAction(file, isNew)));

        // use message if provided.  otherwise, defaults
        String commitMessage = commitMessageOptional
                .orElse(isNew ? commitMessage("Engagement created") : commitMessage("Engagement updated"));

        return CommitMultiple.builder().id(projectId).branch(branch).commitMessage(commitMessage).actions(actions)
                .authorName(authorName).authorEmail(authorEmail).build();

    }

    private Action createAction(File file, boolean isNew) {
        FileAction action = isNew ? FileAction.create : FileAction.update;

        return Action.builder().action(action).filePath(stripPrefix(file.getFilePath())).content(file.getContent())
                .encoding("base64").build();
    }

    private String stripPrefix(String in) {
        if (in != null && in.startsWith(stripPathPrefix)) {
            return in.split(stripPathPrefix)[1];
        }
        return in;
    }
    
    private String commitMessage(String message) {
        return String.format("%s %s %s", message, getEmoji(), getEmoji());
    }

    private String getEmoji() {
        String bear = "\ud83d\udc3b";

        int bearCodePoint = bear.codePointAt(bear.offsetByCodePoints(0, 0));
        int mysteryAnimalCodePoint = bearCodePoint + new SecureRandom().nextInt(144);
        char mysteryEmoji[] = { Character.highSurrogate(mysteryAnimalCodePoint),
                Character.lowSurrogate(mysteryAnimalCodePoint) };

        return String.valueOf(mysteryEmoji);
    }
    
    private String getPath(String customerName, String engagementName) {
        return new StringBuilder(engagementPathPrefix)
                .append("/")
                .append(customerName)
                .append("/")
                .append(engagementName)
                .append("/iac").toString();
    }

}
