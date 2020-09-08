package com.redhat.labs.lodestar.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class GitApiLivenessProbe implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
//        TODO - add something here for app down eg skull for app dead?
        return HealthCheckResponse.up("\uD83D\uDC4D");
    }
}