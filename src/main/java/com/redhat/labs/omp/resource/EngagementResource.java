package com.redhat.labs.omp.resource;

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.gitlab.Project;
import com.redhat.labs.omp.service.EngagementService;

@Path("/api/v1/engagements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EngagementResource {

    public static Logger LOGGER = LoggerFactory.getLogger(EngagementResource.class);

    @Inject
    EngagementService engagementService;

    @POST
    @Counted(name = "engagement", description = "How many engagements request have been requested")
    @Timed(name = "performedCreates", description = "How much time it takes to create an engagement", unit = MetricUnits.MILLISECONDS)
    public Response createEngagement(Engagement engagement, @Context UriInfo uriInfo,
            @NotBlank @QueryParam("username") String author, @NotBlank @QueryParam("userEmail") String authorEmail) {
LOGGER.debug("engagement from resource {}", engagement);
        Project project = engagementService.createEngagement(engagement, author, authorEmail);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(project.getId()));
        return Response.created(builder.build()).build();

    }

    @GET
    @Counted(name = "get-engagement", description = "How many enagement requests have been requested")
    @Timed(name = "performedEngagementGetAll", description = "Time to get all engagements", unit = MetricUnits.MILLISECONDS)
    public Response findAllEngagements() {
        List<Engagement> engagements = engagementService.getAllEngagements();

        return Response.ok().entity(engagements).build();
    }

}
