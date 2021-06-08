package com.redhat.labs.lodestar.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.redhat.labs.lodestar.config.JsonMarshaller;
import com.redhat.labs.lodestar.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.EngagementUser;
import com.redhat.labs.lodestar.models.Status;
import com.redhat.labs.lodestar.models.events.DeleteEngagementEvent;
import com.redhat.labs.lodestar.models.events.DeleteProjectEvent;
import com.redhat.labs.lodestar.models.events.EventType;
import com.redhat.labs.lodestar.models.gitlab.Action;
import com.redhat.labs.lodestar.models.gitlab.Commit;
import com.redhat.labs.lodestar.models.gitlab.CommitMultiple;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.FileAction;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.models.gitlab.ProjectTreeNode;
import com.redhat.labs.lodestar.models.pagination.Page;
import com.redhat.labs.lodestar.utils.GitLabPathUtils;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;

@ApplicationScoped
public class EngagementService {
    public static final Logger LOGGER = LoggerFactory.getLogger(EngagementService.class);

    private static final String DEFAULT_BRANCH = "master";
    private static final String ENGAGEMENT_FILE = "engagement.json";
    private static final String STATUS_FILE = "status.json";
    private static final String USER_MGMT_FILE_PREFIX = "user-management-";
    private static final String USER_MGMT_FILE = USER_MGMT_FILE_PREFIX + "UUID.json";
    private static final String USER_MGMT_FILE_PLACEHOLDER = "UUID";
    private static final String IAC = "iac";

    private String engagementPathPrefix;

    @ConfigProperty(name = "engagements.repository.id")
    Integer engagementRepositoryId;

    @ConfigProperty(name = "stripPathPrefix", defaultValue = "schema/")
    String stripPathPrefix;

    @ConfigProperty(name = "orchestration.queue.directory", defaultValue = "queue")
    String orchestrationQueueDirectory;

    @Inject
    ProjectService projectService;

    @Inject
    GroupService groupService;

    @Inject
    FileService fileService;

    @Inject
    HookService hookService;

    @Inject
    ConfigService configService;

    @Inject
    ProjectStructureService structureService;

    @Inject
    JsonMarshaller json;

    @Inject
    EventBus eventBus;

    @PostConstruct
    public void setPathPrefix() {
        Optional<Group> groupOption = groupService.getGitLabGroupByById(engagementRepositoryId);

        if (groupOption.isPresent()) {
            engagementPathPrefix = groupOption.get().getFullPath();
            LOGGER.info("Engagement repo set- to {}", engagementPathPrefix);
        } else {
            LOGGER.warn("Could not find the path for repo {}", engagementRepositoryId);
        }
    }

    // create an engagement
    public Project createEngagement(Engagement engagement, String author, String authorEmail) {

        // create project structure
        Project project = structureService.createOrUpdateProjectStructure(engagement, engagementPathPrefix);
        engagement.setProjectId(project.getId());

        // get commit message before creating file
        Optional<String> commitMessageOptional = Optional.ofNullable(engagement.getCommitMessage());

        // get all template files
        List<File> repoFiles = new ArrayList<>();
        repoFiles.add(createEngagmentFile(engagement));

        // create user reset file(s) if required
        repoFiles.addAll(createUserManagementFiles(engagement));

        // create actions for multiple commit
        CommitMultiple commit = createCommitMultiple(repoFiles, project.getId(), DEFAULT_BRANCH, author, authorEmail,
                project.isFirst(), commitMessageOptional);

        if (LOGGER.isDebugEnabled()) {
            commit.getActions().stream().forEach(file -> LOGGER.debug("Action File path :: {}", file.getFilePath()));
        }

        // send commit to gitlab
        if (!fileService.createFiles(project.getId(), commit)) {
            throw new UnexpectedGitLabResponseException("failed to commit files for engagement creation.");
        }

        // create hooks if new engagement, should already exist if updating
        if (project.isFirst()) {
            configService.createWebhooksForEnagement(engagement);
        }

        return project;

    }

    public List<Commit> getCommitLog(String customerName, String engagementName) {
        String projectPath = GitLabPathUtils.getValidPath(engagementPathPrefix, customerName, engagementName);
        return projectService.getCommitLog(projectPath);
    }

    public List<Hook> getHooks(String customer, String engagment) {
        Optional<Project> project = getProject(customer, engagment);

        if (project.isPresent()) {
            return hookService.getProjectHooks(project.get().getId());
        }

        return new ArrayList<>();
    }

    public Response createHook(String customerName, String engagementName, Hook hook) {
        Response created = Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST).entity("project doesn't exist")
                .build();
        Optional<Project> optional = getProject(customerName, engagementName);

        if (optional.isPresent()) {
            List<Hook> hooks = hookService.getProjectHooks(optional.get().getId());
            boolean hookExists = hooks.stream().anyMatch(h -> h.getUrl().equals(hook.getUrl()));
            if (!hookExists) {
                created = hookService.createProjectHook(optional.get().getId(), hook);
            }
        }

        return created;
    }

    public void deleteHooks() {

        List<Project> projects = projectService.getProjectsByGroup(engagementRepositoryId, true);
        projects.stream().filter(project -> project.getName().equals(IAC))
                .forEach(project -> hookService.deleteProjectHooks(project.getId()));

    }

    private Status getProjectStatusFile(String customerName, String engagementName) {
        Status status = null;
        Optional<File> file = fileService
                .getFile(GitLabPathUtils.getValidPath(engagementPathPrefix, customerName, engagementName), STATUS_FILE);
        if (file.isPresent()) {
            status = json.fromJson(file.get().getContent(), Status.class);
        }

        return status;
    }

    public Optional<Status> getProjectStatus(String customerName, String engagementName) {

        List<ProjectTreeNode> nodes = projectService
                .getProjectTree(GitLabPathUtils.getValidPath(engagementPathPrefix, customerName, engagementName), false);

        // find status file node or throw 404
        List<ProjectTreeNode> status = nodes.stream().filter(node -> STATUS_FILE.equals(node.getName()))
                .collect(Collectors.toList());
        if (status.isEmpty()) {
            return Optional.empty();
        }

        // get status
        return Optional.of(getProjectStatusFile(customerName, engagementName));

    }

    public Optional<Project> getProject(String customerName, String engagementName) {
        String fullPath = GitLabPathUtils.getValidPath(engagementPathPrefix, customerName, engagementName);

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
    public List<Engagement> getAllEngagements(Optional<Boolean> includeStatus, Optional<Boolean> includeCommits) {

        List<Project> projects = projectService.getProjectsByGroup(engagementRepositoryId, true);
        return getEngagementsFromProject(projects, includeStatus, includeCommits);

    }

    public Page getEngagementPaginated(Optional<Integer> page, Optional<Integer> perPage,
            Optional<Boolean> includeStatus, Optional<Boolean> includeCommits) {

        Page ePage = projectService.getProjectsByGroupPaginated(engagementRepositoryId, true, page, perPage);
        ePage.setEngagements(getEngagementsFromProject(ePage.getResults(), includeStatus, includeCommits));
        return ePage;

    }

    private List<Engagement> getEngagementsFromProject(List<Project> projects, Optional<Boolean> includeStatus,
            Optional<Boolean> includeCommits) {

        boolean status = includeStatus.orElse(true);
        boolean commits = includeCommits.orElse(true);

        return projects.parallelStream().filter(p -> IAC.equals(p.getName()))
                .map(project -> getEngagement(project, status, commits)).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());

    }

    public Engagement getEngagement(String namespaceOrId, boolean includeStatus) {
        Engagement engagement = null;

        Optional<Project> project = projectService.getProjectByIdOrPath(namespaceOrId);

        if (project.isPresent()) {
            engagement = getEngagement(project.get(), includeStatus, true).orElse(null);
        }

        return engagement;
    }

    public Engagement getEngagement(String customerName, String engagementName, boolean includeStatus) {
        Engagement engagement = null;

        Optional<Project> project = getProject(customerName, engagementName);

        if (project.isPresent()) {
            engagement = getEngagement(project.get(), includeStatus, true).orElse(null);
        }

        return engagement;
    }

    public Optional<Engagement> getEngagement(Project project, boolean includeStatus, boolean includeCommits) {
        Engagement engagement = null;

        Optional<File> engagementFile = fileService.getFileAllow404(project.getId(), ENGAGEMENT_FILE);
        if (engagementFile.isPresent()) {
            engagement = json.fromJson(engagementFile.get().getContent(), Engagement.class);

            if (includeCommits) {
                List<Commit> commits = projectService.getCommitLog(String.valueOf(engagement.getProjectId()));
                engagement.setCommits(commits);
            }
        }

        if (includeStatus && engagement != null) {
            Optional<File> statusFile = fileService.getFileAllow404(project.getId(), STATUS_FILE);
            if (statusFile.isPresent()) {
                engagement.setStatus(json.fromJson(statusFile.get().getContent(), Status.class));
            }
        }

        return Optional.ofNullable(engagement);
    }

    public void deleteEngagement(String customerName, String engagementName) {
        // create and send delete engagement event
        eventBus.sendAndForget(EventType.DELETE_ENGAGEMENT_EVENT,
                DeleteEngagementEvent.builder().customerName(customerName).engagementName(engagementName).build());
    }

    private File createEngagmentFile(Engagement engagement) {
        // Git api is read only here.
        engagement.setCommits(null);
        engagement.setStatus(null);
        engagement.setCommitMessage(null);

        String fileContent = json.toJson(engagement);
        return File.builder().content(fileContent).filePath(ENGAGEMENT_FILE).build();
    }

    private List<File> createUserManagementFiles(Engagement engagement) {

        if (null == engagement.getEngagementUsers()) {
            return Lists.newArrayList();
        }

        return engagement.getEngagementUsers().stream().filter(EngagementUser::isReset).filter(user -> fileService
                .getFileAllow404(engagement.getProjectId(), getUserManagementFileName(user.getUuid())).isEmpty())
                .map(user -> File.builder().content(json.toJson(user))
                        .filePath(getUserManagementFileName(user.getUuid())).build())
                .collect(Collectors.toList());

    }

    private String getUserManagementFileName(String uuid) {
        return new StringBuilder(orchestrationQueueDirectory).append("/")
                .append(USER_MGMT_FILE.replace(USER_MGMT_FILE_PLACEHOLDER, uuid)).toString();
    }

    private CommitMultiple createCommitMultiple(List<File> filesToCommit, Integer projectId, String branch,
            String authorName, String authorEmail, boolean isNew, Optional<String> commitMessageOptional) {

        // Split files between user-management files and all others
        Map<Boolean, List<File>> fileMap = filesToCommit.stream()
                .collect(Collectors.partitioningBy(file -> file.getFilePath().contains(USER_MGMT_FILE_PREFIX)));

        // create actions for each user management file
        List<Action> userManagementFiles = fileMap.get(true).stream().map(file -> createAction(file, true))
                .collect(Collectors.toList());

        // create actions for all other files
        List<Action> otherFiles = fileMap.get(false).stream().map(file -> createAction(file, isNew))
                .collect(Collectors.toList());

        // merge the actions
        List<Action> actions = Stream.of(userManagementFiles, otherFiles).flatMap(Collection::stream)
                .collect(Collectors.toList());

        // use message if provided. otherwise, defaults
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
        char[] mysteryEmoji = { Character.highSurrogate(mysteryAnimalCodePoint),
                Character.lowSurrogate(mysteryAnimalCodePoint) };

        return String.valueOf(mysteryEmoji);
    }

    @ConsumeEvent(value = EventType.DELETE_ENGAGEMENT_EVENT)
    void consumeDeleteEngagementEvent(DeleteEngagementEvent event) {

        // get engagement
        Engagement engagement = getEngagement(event.getCustomerName(), event.getEngagementName(), false);

        // only remove if not launched
        if (null != engagement && null == engagement.getLaunch()) {

            // create and send delete event
            eventBus.sendAndForget(EventType.DELETE_PROJECT_EVENT, DeleteProjectEvent.builder().engagement(engagement)
                    .engagementPathPrefix(engagementPathPrefix).build());

        }

    }

}
