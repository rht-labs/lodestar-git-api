package com.redhat.labs.omp.resources;


import com.redhat.labs.omp.models.CreateProjectRequest;
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
import java.util.Map;

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
    @RestClient
    protected GitLabService gitLabService;

    @POST
    // TODO handle exception
    public Object createResidency(Residency residency) throws Exception {
        // create the project
        CreateProjectRequest cpr = new CreateProjectRequest();
        cpr.projectName = residency.projectName;

        GitLabCreateProjectResponse glcpr = projects.createNewProject(cpr);
        residency.id = glcpr.id;
        logger.info("Created project with Id {}", glcpr.id);

        // combobulate the template
        GetMultipleFilesResponse gmfr = combobulator.process(residency.toMap());

        // build the files to be created
        CommitMultipleFilesInRepsitoryRequest cmfirr = new CommitMultipleFilesInRepsitoryRequest();
        for (SingleFileResponse file : gmfr.files) {
            cmfirr.addFileRequest(new CreateCommitFileRequest(FileAction.create, file.fileName, file.fileContent));
        }
        // TODO probably not useful...
        cmfirr.authorEmail = residency.engagementLeadEmail;
        cmfirr.authorName = residency.engagementLeadName;

        cmfirr.commitMessage = "\uD83E\uDD84 Created by OMP Git API \uD83D\uDE80 \uD83C\uDFC1";

        // create the files in the repository
        return gitLabService.createFilesInRepository(glcpr.id, cmfirr);
    }
}
