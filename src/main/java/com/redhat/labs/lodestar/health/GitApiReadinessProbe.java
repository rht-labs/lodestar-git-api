package com.redhat.labs.lodestar.health;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.redhat.labs.lodestar.rest.client.GitLabService;

@Readiness
@ApplicationScoped
public class GitApiReadinessProbe implements HealthCheck{

    @ConfigProperty(name = "engagements.repository.id")
    Integer engagementRepositoryId;

    @Inject
    @RestClient
    GitLabService gitLabService;

    @Override
    public HealthCheckResponse call() {
//        1. check connection to the GitLab API by calling the projects endpoint
        HealthCheckResponseBuilder healthCheckResponseBuilder =  HealthCheckResponse.named("GitLab Connection");

        if (this.checkGitLabConnection()) {
            healthCheckResponseBuilder.up().withData("OK", "\uD83D\uDC4D");
        } else {
            healthCheckResponseBuilder.down().withData("OK", "\uD83D\uDC4E");
        }
//        2. Chcek the connection to Cache to make sure it can be used
        return healthCheckResponseBuilder.build();
    }

    private boolean checkGitLabConnection() {
        return null != gitLabService.getGroupByIdOrPath(String.valueOf(engagementRepositoryId));
    }
}
