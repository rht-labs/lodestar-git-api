package com.redhat.labs.lodestar.models;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

import com.redhat.labs.lodestar.models.gitlab.Commit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Engagement {

    private String uuid;
    private int projectId;
    private String customerName;
    private String projectName;
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private String archiveDate;
    private String engagementLeadName;
    private String engagementLeadEmail;
    private String technicalLeadName;
    private String technicalLeadEmail;
    private String customerContactName;
    private String customerContactEmail;
    private boolean publicReference;
    private String additionalDetails;
    private Launch launch;
    private List<EngagementUser> engagementUsers;
    private List<HostingEnvironment> hostingEnvironments;
    private String timezone;

    private Status status;
    private List<Commit> commits;
    private CreationDetails creationDetails;

    @JsonbProperty("engagement_region")
    private String region;
    @JsonbProperty("engagement_type")
    private String type;

    @JsonbProperty("engagement_categories")
    private List<Category> categories;

    private List<Artifact> artifacts;
    private String commitMessage;
    private List<UseCase> useCases;
    private List<Score> scores;

    @JsonbProperty("billing_codes")
    private List<BillingProject> billingCodes;

}
