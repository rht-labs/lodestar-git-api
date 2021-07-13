package com.redhat.labs.lodestar.resource;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.EngagementProject;
import com.redhat.labs.lodestar.models.Status;
import com.redhat.labs.lodestar.models.gitlab.Commit;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.models.pagination.Page;
import com.redhat.labs.lodestar.service.EngagementService;

@Path("/api/v1/engagements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Engagements", description = "Engagement data")
public class EngagementResource {

    public static final Logger LOGGER = LoggerFactory.getLogger(EngagementResource.class);

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
    public Response findAllEngagements(@Context UriInfo uriInfo, @QueryParam("pagination") Optional<Boolean> pagination,
            @QueryParam("page") Optional<Integer> page, @QueryParam("per_page") Optional<Integer> perPage,
            @QueryParam("includeStatus") Optional<Boolean> includeStatus,
            @QueryParam("includeCommits") Optional<Boolean> includeCommits) {

        ResponseBuilder builder = Response.ok();

        if (pagination.isPresent() && Boolean.TRUE.equals(pagination.get())) {

            Page ePage = engagementService.getEngagementPaginated(page, perPage, includeStatus,
                    includeCommits);
            builder.entity(ePage.getEngagements());
            builder.links(ePage.getLinks(uriInfo.getAbsolutePathBuilder()));
            ePage.getHeaders().entrySet().stream().forEach(e -> builder.header(e.getKey(), e.getValue()));

        } else {
            builder.entity(engagementService.getAllEngagements(includeStatus, includeCommits));
        }

        return builder.build();

    }

    @GET
    @Path("/namespace/{namespace}")
    @Counted(name = "get-engagement-namespace", description = "Count of get by id or namespace")
    @Timed(name = "performedEngagementGetByNamespace", description = "Time to get an engagement by namespace", unit = MetricUnits.MILLISECONDS)
    public Response getEngagement(@PathParam("namespace") String namespace,
            @QueryParam("includeStatus") boolean includeStatus) {

        Engagement response = engagementService.getEngagement(namespace, includeStatus);
        return Response.ok().entity(response).build();
    }

    @GET
    @Path("/customer/{customer}/{engagement}")
    @Counted(name = "get-engagement", description = "Count of get engagement")
    @Timed(name = "performedEngagementGet", description = "Time to get an engagement", unit = MetricUnits.MILLISECONDS)
    public Response getEngagement(@PathParam("customer") String customer, @PathParam("engagement") String engagement,
            @QueryParam("includeStatus") boolean includeStatus) {

        Engagement response = engagementService.getEngagement(customer, engagement, includeStatus);
        return Response.ok().entity(response).build();
    }

    @DELETE
    @Path("/customer/{customer}/{engagement}")
    @Counted(name = "delete-engagement", description = "Count of delete engagement")
    @Timed(name = "performedEngagementDelete", description = "Time to delete an engagement", unit = MetricUnits.MILLISECONDS)
    public Response deleteEngagement(@PathParam("customer") String customer,
            @PathParam("engagement") String engagement) {

        engagementService.deleteEngagement(customer, engagement);
        return Response.accepted().build();

    }

    @POST
    @Path("customer/{customer}/{engagement}/hooks")
    @Counted(name = "create-engagement-hook", description = "Count of create-hook requestst")
    @Timed(name = "performedHookCreate", description = "Time to create hook", unit = MetricUnits.MILLISECONDS)
    @Tag(name = "Hooks")
    public Response createProjectHook(Hook hook, @PathParam("customer") String customer,
            @PathParam("engagement") String engagement) {

        return engagementService.createHook(customer, engagement, hook);

    }

    @GET
    @Path("/customer/{customer}/{engagement}/commits")
    @Counted(name = "get-engagement-commits", description = "Count of get engagement commits")
    @Timed(name = "performedEngagementCommitsGet", description = "Time to get engagement commits", unit = MetricUnits.MILLISECONDS)
    public Response getEngagementCommits(@PathParam("customer") String customer,
            @PathParam("engagement") String engagement) {

        List<Commit> commitList = engagementService.getCommitLog(customer, engagement);
        return Response.ok().entity(commitList).build();
    }

    @GET
    @Path("customer/{customer}/{engagement}/hooks")
    @Counted(name = "get-hook", description = "Count of get-hook requests")
    @Timed(name = "performedHookGetAll", description = "Time to get all hooks", unit = MetricUnits.MILLISECONDS)
    @Tag(name = "Hooks")
    public Response findAllProjectHooks(@PathParam("customer") String customer,
            @PathParam("engagement") String engagement) {

        List<Hook> engagements = engagementService.getHooks(customer, engagement);
        return Response.ok().entity(engagements).build();
    }

    @GET
    @Path("customer/{customer}/{engagement}/status")
    @Counted(name = "get-status", description = "Count of get-status requests")
    @Timed(name = "performedStatusGet", description = "Time to get status", unit = MetricUnits.MILLISECONDS)
    public Response getStatus(@PathParam("customer") String customer, @PathParam("engagement") String engagement) {

        Optional<Status> status = engagementService.getProjectStatus(customer, engagement);
        if(status.isPresent()) {
            return Response.ok().entity(status).build(); 
        }
        return Response.status(404).build();

    }

    @DELETE
    @Path("/hooks")
    @Tag(name = "Hooks")
    @Counted(name = "delete-hooks", description = "Count of delete-hooks requests")
    @Timed(name = "performedHooksDelete", description = "Time to delete hooks", unit = MetricUnits.MILLISECONDS)
    public Response deleteAllHooks() {

        engagementService.deleteHooks();
        return Response.ok().build();

    }
    
    @GET
    @Path("projects/{uuid}")
    @Counted(name = "project-by-uuid", description = "Count of project-by-uuid requests")
    @Timed(name = "performedProjectByUuidGet", description = "Time to get project", unit = MetricUnits.MILLISECONDS)
    @Tag(name = "Projects", description = "Project retrieval")
    public Response getProjectByUuid(@PathParam("uuid") String uuid, @QueryParam("mini") boolean mini) {
        Optional<Project> p = engagementService.getProjectByUuid(uuid);
        
        if(p.isEmpty()) {
            return Response.status(404).build();
        }
        
        if(mini) {
            Project project = p.get();
            EngagementProject ep = EngagementProject.builder().projectId(project.getId()).uuid(project.getDescription()).build();
            return Response.ok(ep).build();
        }
        
        return Response.ok(p.get()).build();
    }
    
    @GET
    @Path("projects")
    @Counted(name = "engagment-uuid-project-id", description = "Count of engagment-uuid-project-id requests")
    @Timed(name = "performedEngagementUuidProjectPairsGet", description = "Time to get all projects engagement uuid", unit = MetricUnits.MILLISECONDS)
    @Tag(name = "Projects", description = "Project retrieval")
    public Response getEngagementUuidProjectPairs() {
        List<EngagementProject> projects = engagementService.getEngagementProjectIdList();
        
        return Response.ok(projects).build();
    }

}
