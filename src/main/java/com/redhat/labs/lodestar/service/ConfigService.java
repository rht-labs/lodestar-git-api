package com.redhat.labs.lodestar.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

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

import io.quarkus.runtime.StartupEvent;
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
     * Loads webhook and configuration data on startup.
     * 
     * @param event
     */
    void onStart(@Observes StartupEvent event) {
        loadWebHookData();
        loadConfigurationData();
    }

    /**
     * Periodically reloads the web hook and configuration data if the configured
     * files have been modified.
     */
    @Scheduled(every = "10s")
    void reloadConfigMapData() {
        loadWebHookData();
        loadConfigurationData();
    }

    /**
     * Reads the configured web hook file and updates the current web hook list if
     * the file has been modified.
     */
    void loadWebHookData() {

        // create config map
        if (null == hookConfigMap) {
            hookConfigMap = ConfigMap.builder().filePath(webHooksFile).build();
            LOGGER.trace("setting webhook config map: {}", hookConfigMap);
        }

        // load content from file
        if (hookConfigMap.updateMountedFile()) {
            LOGGER.trace("loading latest version of webhook file");

            // set hook config as list
            if (hookConfigMap.getContent().isPresent()) {
                hookConfigList = marshaller.fromYaml(hookConfigMap.getContent().get(), HookConfig.class);
                LOGGER.debug("Hook Config List {}", hookConfigList);
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

        projects.stream().filter(project -> project.getName().equals(IAC))
                .filter(project -> !engagementIsArchived(project)).forEach(project -> {

                    hookConfigs.stream().forEach(hookC -> {
                        Hook hook = Hook.builder().projectId(project.getId()).pushEvents(true).url(hookC.getBaseUrl())
                                .token(hookC.getToken()).build();
                        LOGGER.trace("updating project {} \n\twith hook {}", project, hook);
                        hookService.createOrUpdateProjectHook(project.getId(), hook);

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

        Optional<Engagement> engagement = engagementService.getEngagement(project, false);
        if (engagement.isPresent() && null != engagement.get().getArchiveDate()) {

            try {

                ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Z"));
                ZonedDateTime archiveDate = ZonedDateTime.parse(engagement.get().getArchiveDate());

                LOGGER.debug("is past archive date: {}", now.isAfter(archiveDate));
                return now.isAfter(archiveDate);

            } catch (DateTimeParseException dtpe) {
                LOGGER.warn("failed to parse archive date: {}", dtpe.getMessage());
            }

        }

        LOGGER.debug("engagement for project {} not found or failed to parse archive date.", project);
        return false;

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
        if (configurationConfigMap.getContent().isPresent()) {
            configuration = File.builder().filePath(configFile).content(configurationConfigMap.getContent().get())
                    .build();
            LOGGER.debug("Loaded Runtime Config from File, {}", configFile);
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
