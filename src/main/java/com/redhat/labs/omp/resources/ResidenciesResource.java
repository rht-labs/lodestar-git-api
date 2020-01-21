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
    // TODO handle exception
    public Object createResidency(Residency residency) throws Exception {
        // create the git repository structure
        CreateResidencyGroupStructure createResidencyGroupStructure = new CreateResidencyGroupStructure();
        createResidencyGroupStructure.projectName = residency.projectName;
        createResidencyGroupStructure.customerName = residency.customerName;
        GitLabCreateProjectResponse gitLabCreateProjectResponse = groups.createResidencyStructure(createResidencyGroupStructure);

        // combobulate the template
        GetMultipleFilesResponse getMultipleFilesResponse = combobulator.process(residency.toMap());

        // build the files to be created
        CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequest = new CommitMultipleFilesInRepsitoryRequest();
        for (SingleFileResponse file : getMultipleFilesResponse.files) {
            commitMultipleFilesInRepsitoryRequest.addFileRequest(new CreateCommitFileRequest(FileAction.create, file.fileName, file.fileContent));
        }
        // TODO probably not useful...
        commitMultipleFilesInRepsitoryRequest.authorEmail = residency.engagementLeadEmail;
        commitMultipleFilesInRepsitoryRequest.authorName = residency.engagementLeadName;

        commitMultipleFilesInRepsitoryRequest.commitMessage = "\uD83E\uDD84 Created by OMP Git API \uD83D\uDE80 \uD83C\uDFC1";

        // create the files in the repository
        return gitLabService.createFilesInRepository(gitLabCreateProjectResponse.id, commitMultipleFilesInRepsitoryRequest);
    }
}
