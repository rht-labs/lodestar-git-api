package com.redhat.labs.omp.resources;


import com.redhat.labs.omp.models.CreateResidencyGroupStructure;
import com.redhat.labs.omp.models.FileAction;
import com.redhat.labs.omp.models.GitLabCreateProjectResponse;
import com.redhat.labs.omp.models.Residency;
import com.redhat.labs.omp.models.filesmanagement.CommitMultipleFilesInRepsitoryRequest;
import com.redhat.labs.omp.models.filesmanagement.CreateCommitFileRequest;
import com.redhat.labs.omp.models.filesmanagement.GetMultipleFilesResponse;
import com.redhat.labs.omp.models.filesmanagement.SingleFileResponse;
import com.redhat.labs.omp.services.GitLabService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/residencies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResidenciesResource {
    public static Logger logger = LoggerFactory.getLogger(ResidenciesResource.class);

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
    @Counted(name = "residencies", description = "How many residencies reuquest have been requested")
    @Timed(name = "performedChecks", description = "How much time it takes to create residency", unit = MetricUnits.MILLISECONDS)
    public Object createResidency(Residency residency) {
        GitLabCreateProjectResponse gitLabCreateProjectResponse = createGitLabProject(residency);

        GetMultipleFilesResponse getMultipleFilesResponse = combobulator.process(residency.toMap());

        CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequest = getCommitMultipleFilesInRepositoryRequest(residency, getMultipleFilesResponse);

        return gitLabService.createFilesInRepository(gitLabCreateProjectResponse.id, commitMultipleFilesInRepsitoryRequest);
    }

    private CommitMultipleFilesInRepsitoryRequest getCommitMultipleFilesInRepositoryRequest(Residency residency, GetMultipleFilesResponse getMultipleFilesResponse) {
        CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequest = new CommitMultipleFilesInRepsitoryRequest();
        getMultipleFilesResponse.files.stream().forEach(f -> {
            // TODO improve this a bit
            String[] splitFilePath = f.fileName.split(stripPathPrefix);
            if (splitFilePath.length >= 1) {
                commitMultipleFilesInRepsitoryRequest.addFileRequest(new CreateCommitFileRequest(FileAction.create, splitFilePath[1], f.fileContent));
            } else {
                commitMultipleFilesInRepsitoryRequest.addFileRequest(new CreateCommitFileRequest(FileAction.create, f.fileName, f.fileContent));
            }
        });
        commitMultipleFilesInRepsitoryRequest.authorEmail = residency.engagementLeadEmail;
        commitMultipleFilesInRepsitoryRequest.authorName = residency.engagementLeadName;
        commitMultipleFilesInRepsitoryRequest.commitMessage = "\uD83E\uDD84 Created by OMP Git API \uD83D\uDE80 \uD83C\uDFC1";
        return commitMultipleFilesInRepsitoryRequest;
    }

    private GitLabCreateProjectResponse createGitLabProject(Residency residency) {
        CreateResidencyGroupStructure createResidencyGroupStructure = new CreateResidencyGroupStructure();
        createResidencyGroupStructure.projectName = residency.projectName;
        createResidencyGroupStructure.customerName = residency.customerName;
        return groups.createResidencyStructure(createResidencyGroupStructure);
    }
}
