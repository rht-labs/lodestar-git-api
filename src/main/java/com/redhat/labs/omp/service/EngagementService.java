package com.redhat.labs.omp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.omp.config.JsonMarshaller;
import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.gitlab.Action;
import com.redhat.labs.omp.models.gitlab.CommitMultiple;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.models.gitlab.FileAction;
import com.redhat.labs.omp.models.gitlab.Group;
import com.redhat.labs.omp.models.gitlab.Project;
import com.redhat.labs.omp.models.gitlab.ProjectSearchResults;

@ApplicationScoped
public class EngagementService {
    public static Logger LOGGER = LoggerFactory.getLogger(EngagementService.class);

    private static final String ENGAGEMENT_PROJECT_NAME = "iac";
    private final String DEFAULT_BRANCH = "master";

    @ConfigProperty(name = "residenciesParentRepositoryId")
    Integer engagementParentId;

    @ConfigProperty(name = "stripPathPrefix", defaultValue = "schema/")
    String stripPathPrefix;

    @ConfigProperty(name = "deployKey")
    Integer deployKey;

    @Inject
    ProjectService projectService;

    @Inject
    GroupService groupService;

    @Inject
    TemplateService templateService;

    @Inject
    FileService fileService;

    @Inject
    JsonMarshaller json;

    // create an engagement
    public Project createEngagement(Engagement engagement, String author, String authorEmail) {

        // create project structure
        Project project = createProjectStucture(engagement);
        engagement.setProjectId(project.getId());

        // get all template files
        List<File> templateFiles = new ArrayList<>();
        templateFiles.add(createEngagmentFile(engagement));

        if (LOGGER.isDebugEnabled()) {
            templateFiles.stream().forEach(file -> LOGGER.debug("File path :: " + file.getFilePath()));
        }

        // create actions for multiple commit
        CommitMultiple commit = createCommitMultiple(templateFiles, project.getId(), DEFAULT_BRANCH, author,
                authorEmail, project.isFirst());

        if (LOGGER.isDebugEnabled()) {
            commit.getActions().stream().forEach(file -> LOGGER.debug("Action File path :: " + file.getFilePath()));
        }

        // send commit to gitlab
        if (!fileService.createFiles(project.getId(), commit)) {
            throw new UnexpectedGitLabResponseException("failed to commit files for engagement creation.");
        }

        return project;

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

        List<ProjectSearchResults> projects = projectService.getAllProjectsByNane("iac");

        List<Engagement> engagementFiles = new ArrayList<>();

        for (ProjectSearchResults project : projects) {
            LOGGER.debug("project id {}", project.getId());
            Optional<File> engagementFile = fileService.getFileAllow404(project.getId(), "engagement.json");
            if (engagementFile.isPresent()) {
                engagementFiles.add(json.fromJson(engagementFile.get().getContent(), Engagement.class));
            }
        }

        return engagementFiles;
    }

    private File createEngagmentFile(Engagement engagement) {
        String fileContent = json.toJson(engagement);
        File file = File.builder().content(fileContent).filePath("engagement.json").build();

        return file;
    }

    private Project createProjectStucture(Engagement engagement) {

        // create group for customer name
        Group customerGroup = getOrCreateGroup(engagement.getCustomerName(),
                Group.builder().name(engagement.getCustomerName()).path(engagement.getCustomerName())
                        .parentId(engagementParentId).build());

        // create group for project name
        Group projectGroup = getOrCreateGroup(engagement.getProjectName(),
                Group.builder().name(engagement.getProjectName()).path(engagement.getProjectName())
                        .parentId(customerGroup.getId()).build());

        // create project under project name group
        Project project = getOrCreateProject(projectGroup.getId(), ENGAGEMENT_PROJECT_NAME, Project.builder()
                .name(ENGAGEMENT_PROJECT_NAME).visibility("private").namespaceId(projectGroup.getId()).build());

        // enable deployment key on project
        projectService.enableDeploymentKeyOnProject(project.getId(), deployKey);

        return project;

    }

    private Group getOrCreateGroup(String groupName, Group groupToCreate) {

        Optional<Group> optional = groupService.getGitLabGroupByName(groupName);

        if (!optional.isPresent()) {

            // try to create group
            optional = groupService.createGitLabGroup(groupToCreate);

            if (!optional.isPresent()) {
                throw new UnexpectedGitLabResponseException("failed to create group");
            }

        }

        return optional.get();

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
            String authorName, String authorEmail, boolean isNew) {

        List<Action> actions = new ArrayList<>();

        // convert each file to action - parallelStream was bringing inconsistent
        // results
        filesToCommit.stream().forEach(file -> actions.add(createAction(file, isNew)));

        return CommitMultiple.builder().id(projectId).branch(branch).commitMessage(commitMessage()).actions(actions)
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

    private String commitMessage() {
        String COMMIT_MSG = "%s engagement update by git-api %s ";
        return String.format(COMMIT_MSG, getEmoji(), getEmoji());
    }

    private String getEmoji() {
        String bear = "\ud83d\udc3b";

        int bearCodePoint = bear.codePointAt(bear.offsetByCodePoints(0, 0));
        int mysteryAnimalCodePoint = bearCodePoint + new Random().nextInt(144);
        char mysteryEmoji[] = { Character.highSurrogate(mysteryAnimalCodePoint),
                Character.lowSurrogate(mysteryAnimalCodePoint) };

        return String.valueOf(mysteryEmoji);
    }

    // update an engagement

    // get an engagement

    // delete an engagement?

}
