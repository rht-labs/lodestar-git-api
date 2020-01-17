package com.redhat.labs.omp.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GitLabConfig {
    // defined as a static method because we need access to the token from @ClientHeaderParam
    public static String getPersonalAccessToken() {
        return System.getenv().get("GITLAB_PERSONAL_ACCESS_TOKEN");
    }
}
