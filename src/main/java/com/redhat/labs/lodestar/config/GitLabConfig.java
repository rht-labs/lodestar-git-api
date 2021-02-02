package com.redhat.labs.lodestar.config;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterForReflection
public class GitLabConfig {
    public static final String GITLAB_PERSONAL_ACCESS_TOKEN = "GITLAB_PERSONAL_ACCESS_TOKEN";
    public static final Logger LOGGER = LoggerFactory.getLogger(GitLabConfig.class);

    private GitLabConfig() {
        throw new IllegalStateException("Utility class");
    }

    // defined as a static method because we need access to the token from @ClientHeaderParam
    public static String getPersonalAccessToken() {
        String gitLabPersonalToken = System.getProperty(GITLAB_PERSONAL_ACCESS_TOKEN);
        if (gitLabPersonalToken == null) {
            gitLabPersonalToken = System.getenv().get(GITLAB_PERSONAL_ACCESS_TOKEN);
        }

        if (gitLabPersonalToken == null) {
            LOGGER.warn(GITLAB_PERSONAL_ACCESS_TOKEN + " environment variable/system property not set!");
        }

        return gitLabPersonalToken;
    }
}
