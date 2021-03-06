package com.redhat.labs.lodestar.resource;

import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.redhat.labs.lodestar.service.MigrationService;

@Path("/api/migrate")
@Tag(name = "Migration", description = "Migratiion services")
public class MigrationResource {

    @Inject
    MigrationService migrationService;

    @PUT
    @Counted(name = "migration", description = "How many migation requests have been invoked")
    @Timed(name = "performedMigration", description = "How much time it takes to migrate", unit = MetricUnits.MILLISECONDS)
    public Response migrate(@QueryParam(value = "participants") boolean migrateParticipants,
            @QueryParam(value = "artifacts") boolean migrateArtifacts,
            @QueryParam(value = "uuids") boolean migrateUuids) {
        
        migrationService.migrate(migrateUuids, migrateParticipants, migrateArtifacts);
        
        return Response.ok().build();

    }
}
