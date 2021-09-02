package com.redhat.labs.lodestar.resource;

import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.redhat.labs.lodestar.service.MigrationService;

@Path("/api/migrate")
@Tag(name = "Migration", description = "Migration services")
public class MigrationResource {

    @Inject
    MigrationService migrationService;

    @PUT
    @Timed(name = "performedMigration", description = "How much time it takes to migrate", unit = MetricUnits.MILLISECONDS)
    public Response migrate(@QueryParam(value = "participants") boolean migrateParticipants,
            @QueryParam("artifacts") boolean migrateArtifacts,
            @QueryParam("uuids") boolean migrateUuids,
            @QueryParam("hosting") boolean migrateHosting,
            @QueryParam("engagements") boolean migrateEngagements,
            @QueryParam("overwrite") boolean overwrite,
            @QueryParam("uuid") String uuid) {
        
        migrationService.migrate(migrateUuids, migrateParticipants, migrateArtifacts, migrateHosting, migrateEngagements,
                overwrite, uuid);
        
        return Response.ok().build();

    }
}
