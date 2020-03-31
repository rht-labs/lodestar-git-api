package com.redhat.labs.omp.models.gitlab;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @JsonbProperty("id")
    private Integer id;
    @NotBlank
    @JsonbProperty("name")
    private String name;
    @NotBlank
    @JsonbProperty("path")
    private String path;
    @JsonbProperty("description")
    private String description;
    @JsonbProperty("membership_lock")
    private Boolean membershipLock;
    @JsonbProperty("visibility")
    private String visibility;
    @JsonbProperty("share_with_group_lock")
    private Boolean shareWithGroupLock;
    @JsonbProperty("require_two_factor_authentication")
    private Boolean requireTwoFactorAuthentication;
    @JsonbProperty("two_factor_grace_period")
    private Integer twoFactorAuthenticationGracePeriod;
    @JsonbProperty("project_creation_level")
    private String projectCreationLevel;

    @JsonbProperty("auto_devops_enabled")
    private Boolean AutoDevopsEnabled;
    @JsonbProperty("subgroup_creation_level")
    private String subgroupCreationLevel;
    @JsonbProperty("emails_disabled")
    private Boolean emailsDisabled;
    @JsonbProperty("mentions_disabled")
    private Boolean mentionsDisabled;
    @JsonbProperty("lfs_enabled")
    private Boolean lfsEnabled;
    @JsonbProperty("request_access_enabled")
    private Boolean requestAccessEnabled;
    @JsonbProperty("parent_id")
    private Integer parentId;

    @JsonbProperty("default_branch_protection")
    private Integer defaultBranchProtection;
    @JsonbProperty("shared_runners_minutes_limit")
    private Integer sharedRunnersMinuteLimit;
    @JsonbProperty("extra_shared_runners_minutes_limit")
    private Integer extraSharedRunnersMinutesLimit;

    // TODO: Can expose avatar as well, just need to figure out what type to use.
    // avatar mixed no Image file for avatar of the group. Introduced in GitLab 12.9

}
