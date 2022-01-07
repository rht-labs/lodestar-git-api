package com.redhat.labs.lodestar.resource;

import javax.inject.Inject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.redhat.labs.lodestar.service.MigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Path("/api/migrate")
@Tag(name = "Migration", description = "Migration services")
public class MigrationResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationResource.class);

    @Inject
    MigrationService migrationService;

    @PUT
    @Timed(name = "performedMigration", description = "How much time it takes to migrate", unit = MetricUnits.MILLISECONDS)
    @Produces("application/json")
    public Response migrate(@QueryParam(value = "participants") boolean migrateParticipants,
            @QueryParam("artifacts") boolean migrateArtifacts,
            @QueryParam("projects") boolean migrateUuids,
            @QueryParam("hosting") boolean migrateHosting,
            @QueryParam("engagements") boolean migrateEngagements,
            @QueryParam("overwrite") boolean overwrite,
            @QueryParam("dryRun") boolean dryRun,
            @QueryParam("uuids") List<String> uuids) {

        try {
            LOGGER.debug("migration uuid {} participants {} artifacts {} hosting {} engagements {} overwrite {} dry run {} uuids {}",
                    migrateUuids, migrateParticipants, migrateArtifacts, migrateHosting, migrateEngagements, overwrite, dryRun, uuids.size());
            Map<String, String> failures = migrationService.migrate(migrateUuids, migrateParticipants, migrateArtifacts, migrateHosting, migrateEngagements,
                    overwrite, dryRun, uuids);
            return Response.ok(failures).build();
        } catch (Exception ex) {
            LOGGER.error("Migration did not complete successfully", ex);
            return Response.status(Response.Status.BAD_REQUEST).entity("{ \"message\": \"Migration did not complete successfully\"}").build();
        }
    }
}
