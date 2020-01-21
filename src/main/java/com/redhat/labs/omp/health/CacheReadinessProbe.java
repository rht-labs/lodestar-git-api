package com.redhat.labs.omp.health;


import com.redhat.labs.cache.cacheStore.ResidencyDataCache;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Readiness
@ApplicationScoped
public class CacheReadinessProbe implements HealthCheck{

    @Inject
    ResidencyDataCache residencyDataCache;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder healthCheckResponseBuilder =  HealthCheckResponse.named("Cache Connection");
        if (this.checkCacheConnection()) {
            healthCheckResponseBuilder.up().withData("OK", "\uD83D\uDC4D");
        } else {
            healthCheckResponseBuilder.down().withData("OK", "\uD83D\uDC4E");
        }
        return healthCheckResponseBuilder.build();
    }

    private boolean checkCacheConnection() {
        return residencyDataCache.getRemoteCacheManager().isStarted();
    }
}
