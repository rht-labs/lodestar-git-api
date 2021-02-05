package com.redhat.labs.lodestar.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.lodestar.config.JsonMarshaller;
import com.redhat.labs.lodestar.exception.FileNotFoundException;
import com.redhat.labs.lodestar.models.ConfigMap;
import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.HookConfig;
import com.redhat.labs.lodestar.models.gitlab.Project;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class ConfigService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    private static final String IAC = "iac";

    @ConfigProperty(name = "config.file")
    String configFile;

    @ConfigProperty(name = "webhook.file")
    String webHooksFile;

    @ConfigProperty(name = "engagements.repository.id")
    Integer engagementRepositoryId;

    @ConfigProperty(name = "config.repository.id", defaultValue = "9407")
    String configRepositoryId;

    @ConfigProperty(name = "config.gitlab.ref", defaultValue = "master")
    String gitRef;

    @ConfigProperty(name = "config.reload")
    boolean reloadConfig;

    ConfigMap hookConfigMap;
    ConfigMap configurationConfigMap;

    List<HookConfig> hookConfigList;
    File configuration;

    @Inject
    ProjectService projectService;

    @Inject
    FileService fileService;

    @Inject
    HookService hookService;

    @Inject
    EngagementService engagementService;

    @Inject
    JsonMarshaller marshaller;

    /**
     * Periodically reloads the web hook and configuration data if the configured
     * files have been modified.
     */
    @Scheduled(every = "30s")
    void reloadConfigMapData() {
        if (reloadConfig) {
            loadWebHookData();
            loadConfigurationData();
        }
    }

    /**
     * Reads the configured web hook file and updates the current web hook list if
     * the file has been modified.
     */
    void loadWebHookData() {

        // create config map
        if (null == hookConfigMap) {
            hookConfigMap = ConfigMap.builder().filePath(webHooksFile).build();
            LOGGER.debug("setting webhook config map: {}", hookConfigMap);
        }

        // load content from file
        if (hookConfigMap.updateMountedFile()) {

            // set hook config as list
            Optional<String> content = hookConfigMap.getContent();
            if (content.isPresent()) {
                hookConfigList = marshaller.fromYaml(content.get(), HookConfig.class);
                LOGGER.debug("Loaded Hook Config List {}", hookConfigList);
            }

            // update web hooks
            updateWebHooksInGitLab();

        }

    }

    /**
     * Updates the web hooks in GitLab for each non archived project in the
     * configured engagement group.
     */
    void updateWebHooksInGitLab() {

        List<HookConfig> hookConfigs = getHookConfig();
        List<Project> projects = projectService.getProjectsByGroup(engagementRepositoryId, true);
        LOGGER.debug("number of projects found: {}", projects.size());
        projects.stream().filter(project -> project.getName().equals(IAC))
                .filter(project -> !engagementIsArchived(project)).forEach(project -> {

                    Integer projectId = project.getId();

                    LOGGER.debug("updating project: {}",
                            (null != project.getNamespace()) ? project.getNamespace().getFullPath() : projectId);

                    // remove existing webhooks for project
                    hookService.deleteProjectHooks(projectId);

                    // create hooks from configuration
                    hookConfigs.stream().forEach(hookC -> {
                        Hook hook = Hook.builder().projectId(projectId).pushEvents(true).url(hookC.getBaseUrl())
                                .token(hookC.getToken()).build();
                        LOGGER.debug("\tcreating webhook {}", hook.getUrl());
                        Response response = hookService.createProjectHook(projectId, hook);
                        LOGGER.debug("\t\tservice response code: {}", response.getStatus());
                        response.close();

                    });

                });

    }

    /**
     * Returns true if the current time is after the archived date in the
     * engagement.json for the given project.
     * 
     * @param project
     * @return
     */
    boolean engagementIsArchived(Project project) {

        Optional<Engagement> engagement = engagementService.getEngagement(project, false, false);
        if (engagement.isPresent() && null != engagement.get().getArchiveDate()) {

            Engagement e = engagement.get();

            try {

                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Z"));
                ZonedDateTime archiveDate = ZonedDateTime.parse(e.getArchiveDate());

                boolean isPast = now.isAfter(archiveDate);

                LOGGER.debug("{}:{} is past archive date: {}", e.getCustomerName(), e.getProjectName(), isPast);
                return isPast;

            } catch (DateTimeParseException dtpe) {
                LOGGER.warn("failed to parse archive date: {}, for {}:{}", dtpe.getMessage(), e.getCustomerName(),
                        e.getProjectName());
            }

        }

        return false;

    }

    /**
     * Adds all configured webhooks to the project for the given {@link Engagement}.
     * 
     * @param engagement
     * 
     */
    public void createWebhooksForEnagement(Engagement engagement) {

        List<HookConfig> hookConfigs = getHookConfig();
        hookConfigs.stream().forEach(hookC -> {
            Hook hook = Hook.builder().projectId(engagement.getProjectId()).pushEvents(true).url(hookC.getBaseUrl())
                    .token(hookC.getToken()).build();
                hookService.createProjectHook(engagement.getProjectId(), hook);
        });

    }

    /**
     * Loads the configuration data from the configured file if it has been
     * modified.
     */
    void loadConfigurationData() {

        // create config map
        if (null == configurationConfigMap) {
            configurationConfigMap = ConfigMap.builder().filePath(configFile).build();
        }
        // load initial content
        configurationConfigMap.updateMountedFile();
        // create file
        Optional<String> content = configurationConfigMap.getContent();
        if (content.isPresent()) {
            configuration = File.builder().filePath(configFile).content(content.get())
                    .build();
        }

    }

    /**
     * Returns a {@link File} containging the configuration data from the configured
     * file. If the configured file is not available, the data is loaded from
     * GitLab.
     * 
     * @return
     */
    public File getConfigFile() {

        if (null != configuration) {
            return configuration;
        }

        String gitLabConfigFile = configFile.charAt(0) == '/' ? configFile.substring(1) : configFile;
        Optional<File> optional = fileService.getFile(configRepositoryId, gitLabConfigFile, gitRef);

        if (!optional.isPresent()) {
            throw new FileNotFoundException("the configured file was not found in the gitlab repository.");
        }

        return optional.get();
    }

    /**
     * Returns a {@link List} of {@link HookConfig} from the configured file. If the
     * configured file is not available, the data is loaded from GitLab.
     * 
     * @return
     */
    public List<HookConfig> getHookConfig() {

        if (hookConfigList != null) {
            return hookConfigList;
        }

        String gitLabHookFile = webHooksFile.charAt(0) == '/' ? webHooksFile.substring(1) : webHooksFile;
        Optional<File> optional = fileService.getFile(configRepositoryId, gitLabHookFile, gitRef);

        if (!optional.isPresent()) {
            LOGGER.error("No webhook file could be found. This is abnormal but not a deal breaker");
            return new ArrayList<>();
        }

        File file = optional.get();

        return marshaller.fromYaml(file.getContent(), HookConfig.class);

    }

}
