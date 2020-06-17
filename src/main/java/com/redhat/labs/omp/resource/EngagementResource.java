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
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.Status;
import com.redhat.labs.omp.models.gitlab.Commit;
import com.redhat.labs.omp.models.gitlab.Hook;
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

        Project project = engagementService.createEngagement(engagement, author, authorEmail);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(project.getId()));
        return Response.created(builder.build()).build();

    }

    @GET
    @Counted(name = "get-all-engagement", description = "Count of get all engagements")
    @Timed(name = "performedEngagementGetAll", description = "Time to get all engagements", unit = MetricUnits.MILLISECONDS)
    public Response findAllEngagements() {
        
        List<Engagement> engagements = engagementService.getAllEngagements();
        return Response.ok().entity(engagements).build();
    }
    
    @GET
    @Path("/customer/{customer}/{engagement}")
    @Counted(name = "get-engagement", description = "Count of get engagement")
    @Timed(name = "performedEngagementGet", description = "Time to get an engagement", unit = MetricUnits.MILLISECONDS)
    public Response getEngagement(@PathParam("customer") String customer, @PathParam("engagement") String engagement, @QueryParam("includeStatus") boolean includeStatus) {
        
        Engagement response = engagementService.getEngagement(customer, engagement, includeStatus);
        return Response.ok().entity(response).build();
    }
    
    @POST
    @Path("customer/{customer}/{engagement}/hooks")
    @Counted(name = "create-engagement-hook", description = "Count of create-hook requestst")
    @Timed(name = "performedHookCreate", description = "Time to create hook", unit = MetricUnits.MILLISECONDS)
    public Response createProjectHook(Hook hook, @PathParam("customer") String customer, @PathParam("engagement") String engagement) {
        
        Response response = engagementService.createHook(customer, engagement, hook);
        return response;
    }
    
    @GET
    @Path("/customer/{customer}/{engagement}/commits")
    @Counted(name = "get-engagement-commits", description = "Count of get engagement commits")
    @Timed(name = "performedEngagementCommitsGet", description = "Time to get engagement commits", unit = MetricUnits.MILLISECONDS)
    public Response getEngagementCommits(@PathParam("customer") String customer, @PathParam("engagement") String engagement) {
        
        List<Commit> commitList = engagementService.getCommitLog(customer, engagement);
        return Response.ok().entity(commitList).build();
    }
    
    @GET
    @Path("customer/{customer}/{engagement}/hooks")
    @Counted(name = "get-hook", description = "Count of get-hook requests")
    @Timed(name = "performedHookGetAll", description = "Time to get all hooks", unit = MetricUnits.MILLISECONDS)
    public Response findAllProjectHooks(@PathParam("customer") String customer, @PathParam("engagement") String engagement) {
        
        List<Hook> engagements = engagementService.getHooks(customer, engagement);
        return Response.ok().entity(engagements).build();
    }
    
    @GET
    @Path("customer/{customer}/{engagement}/status")
    @Counted(name = "get-status", description = "Count of get-status requests")
    @Timed(name = "performedStatusGet", description = "Time to get status", unit = MetricUnits.MILLISECONDS)
    public Response getStatus(@PathParam("customer") String customer, @PathParam("engagement") String engagement) {
        
        Status status = engagementService.getProjectStatus(customer, engagement);
        return Response.ok().entity(status).build();
    }

}
