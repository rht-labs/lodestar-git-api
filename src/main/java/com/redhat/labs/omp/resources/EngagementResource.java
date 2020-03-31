package com.redhat.labs.omp.resources;

import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.omp.models.CreateResidencyGroupStructure;
import com.redhat.labs.omp.models.FileAction;
import com.redhat.labs.omp.models.Engagement;
import com.redhat.labs.omp.models.gitlab.request.CommitMultipleFilesInRepsitoryRequest;
import com.redhat.labs.omp.models.gitlab.request.CreateCommitFileRequest;
import com.redhat.labs.omp.models.gitlab.response.GetMultipleFilesResponse;
import com.redhat.labs.omp.models.gitlab.response.GitLabCreateProjectResponse;
import com.redhat.labs.omp.rest.client.GitLabService;
import com.redhat.labs.omp.utils.TemplateCombobulator;

@Path("/api/residencies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EngagementResource {
    public static Logger LOGGER = LoggerFactory.getLogger(EngagementResource.class);

    @ConfigProperty(name = "stripPathPrefix", defaultValue = "schema/")
    protected String stripPathPrefix;

    @Inject
    protected TemplateCombobulator combobulator;

    @Inject
    protected ProjectsResource projects;

    @Inject
    protected GroupsResource groups;

    @Inject
    @RestClient
    protected GitLabService gitLabService;

    @POST
    @Counted(name = "engagement", description = "How many engagements request have been requested")
    @Timed(name = "performedChecks", description = "How much time it takes to create an engagement", unit = MetricUnits.MILLISECONDS)
    public Response createEngagement(Engagement engagement, @Context UriInfo uriInfo) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}",
                    JsonbBuilder
                            .create(new JsonbConfig()
                                    .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES))
                            .toJson(engagement));
        }
        GitLabCreateProjectResponse gitLabCreateProjectResponse = createGitLabProject(engagement);
        engagement.id = gitLabCreateProjectResponse.id;

        GetMultipleFilesResponse getMultipleFilesResponse = combobulator.process(engagement);

        CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequest = getCommitMultipleFilesInRepositoryRequest(
                engagement, getMultipleFilesResponse);

        Response gitResponse = gitLabService.createFilesInRepository(gitLabCreateProjectResponse.id,
                commitMultipleFilesInRepsitoryRequest);

        if (gitResponse.getStatus() == 201) {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Integer.toString(gitLabCreateProjectResponse.id));
            return Response.created(builder.build()).build();
        }

        return gitResponse;

    }

    private CommitMultipleFilesInRepsitoryRequest getCommitMultipleFilesInRepositoryRequest(Engagement engagement,
            GetMultipleFilesResponse getMultipleFilesResponse) {
        CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequest = new CommitMultipleFilesInRepsitoryRequest();
        getMultipleFilesResponse.files.stream().forEach(f -> {
            commitMultipleFilesInRepsitoryRequest.addFileRequest(
                    new CreateCommitFileRequest(FileAction.create, stripPrefix(f.getFileName()), f.getFileContent()));
        });
        commitMultipleFilesInRepsitoryRequest.authorEmail = engagement.engagementLeadEmail;
        commitMultipleFilesInRepsitoryRequest.authorName = engagement.engagementLeadName;
        commitMultipleFilesInRepsitoryRequest.commitMessage = "\uD83E\uDD84 Created by OMP Git API \uD83D\uDE80 \uD83C\uDFC1";
        return commitMultipleFilesInRepsitoryRequest;
    }

    public String stripPrefix(String in) {
        if (in != null && in.startsWith(stripPathPrefix)) {
            return in.split(stripPathPrefix)[1];
        }
        return in;
    }

    private GitLabCreateProjectResponse createGitLabProject(Engagement residency) {
        CreateResidencyGroupStructure createResidencyGroupStructure = new CreateResidencyGroupStructure();
        createResidencyGroupStructure.projectName = residency.projectName;
        createResidencyGroupStructure.customerName = residency.customerName;
        return groups.createResidencyStructure(createResidencyGroupStructure);
    }
}
