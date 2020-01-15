package com.rht_labs.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;

@ApplicationScoped
public class GitLabConfig {
    //  TODO - figure out what went wrong with this approach
    @ConfigProperty(name = "gitlab.personal.access.token")
    public String personalAccessToken;

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public static String getGitLabPersonalAccessToken() {
        return CDI.current().select(GitLabConfig.class).get().getPersonalAccessToken();
    }
}
