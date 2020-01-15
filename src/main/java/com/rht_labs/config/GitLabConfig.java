package com.rht_labs.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;

@ApplicationScoped
@ConfigProperties(prefix = "gitlab")
public class GitLabConfig {
    @ConfigProperty(name = "personal.access.token")
    private String personalAccessToken;

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public static String getGitLabPersonalAccessToken() {
        return CDI.current().select(GitLabConfig.class).get().getPersonalAccessToken();
    }
}
