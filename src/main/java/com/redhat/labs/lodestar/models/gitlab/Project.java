package com.redhat.labs.lodestar.models.gitlab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private static final String DO_NOT_DELETE = "DO_NOT_DELETE";

    @JsonbProperty("id")
    private Integer id;
    @NotBlank
    @JsonbProperty("name")
    private String name;
    @NotBlank
    @JsonbProperty("path")
    private String path;
    @JsonbProperty("namespace_id")
    private Integer namespaceId;
    @JsonbProperty("default_branch")
    private String defaultBranch;
    @JsonbProperty("description")
    private String description;
    @JsonbProperty("issues_enabled")
    private boolean issuesEnabled;
    @JsonbProperty("merge_requests_enabled")
    private boolean mergeRequestsEnabled;
    @JsonbProperty("jobs_enabled")
    private boolean jobsEnabled;
    @JsonbProperty("wiki_enabled")
    private boolean wikiEnabled;
    @JsonbProperty("snippets_enabled")
    private boolean snippetsEnabled;
    @JsonbProperty("issues_access_level")
    private String issuesAccessLevel;
    @JsonbProperty("repository_access_level")
    private String repositoryAccessLevel;
    @JsonbProperty("merge_requests_access_level")
    private String mergeRequestsAccessLevel;
    @JsonbProperty("forking_access_level")
    private String forkingAccessLevel;
    @JsonbProperty("builds_access_level")
    private String buildsAccessLevel;
    @JsonbProperty("wiki_access_level")
    private String wikiAccessLevel;
    @JsonbProperty("snippets_access_level")
    private String snippetsAccessLevel;
    @JsonbProperty("pages_access_level")
    private String pagesAccessLevel;
    @JsonbProperty("emails_disabled")
    private Boolean emailsDisabled;
    @JsonbProperty("resolve_outdated_diff_discussions")
    private Boolean resolveOutdatedDiffDiscussions;
    @JsonbProperty("container_registry_enabled")
    private boolean containerRegistryEnabled;
    @JsonbProperty("container_expiration_policy_attributes")
    private Map<String, String> containerExpirationPolicyAttributes;
    @JsonbProperty("shared_runners_enabled")
    private boolean sharedRunnersEnabled;
    @JsonbProperty("visibility")
    private String visibility;
    @JsonbProperty("import_url")
    private String importUrl;
    @JsonbProperty("public_builds")
    private Boolean publicBuilds;
    @JsonbProperty("only_allow_merge_if_pipeline_succeeds")
    private Boolean onlyAllowMergeIfPipelineSucceeds;
    @JsonbProperty("only_allow_merge_if_all_discussions_are_resolved")
    private Boolean onlyAllowMergeIfAllDiscussionsAreResolved;
    @JsonbProperty("merge_method")
    private String mergeMethod;
    @JsonbProperty("autoclose_referenced_issues")
    private Boolean autoCloseReferencedIssues;
    @JsonbProperty("remove_source_branch_after_merge")
    private Boolean removeSourceBranchAfterMerge;
    @JsonbProperty("lfs_enabled")
    private Boolean lfsEnabled;
    @JsonbProperty("request_access_enabled")
    private Boolean requestAccessEnabled;
    @JsonbProperty("tag_list")
    private List<String> tagList;
    @JsonbProperty("printing_merge_request_link_enabled")
    private boolean printingMergeRequestLinkEnabled;
    @JsonbProperty("build_git_strategy")
    private String buildGitStrategy;
    @JsonbProperty("build_timeout")
    private Integer buildTimeout;
    @JsonbProperty("auto_cancel_pending_pipelines")
    private String autoCancelPendingPipelines;
    @JsonbProperty("build_coverage_regex")
    private String buildCoverageRegex;
    @JsonbProperty("ci_config_path")
    private String ciConfigPath;
    @JsonbProperty("auto_devops_enabled")
    private boolean autoDevopsEnabled;
    @JsonbProperty("auto_devops_deploy_strategy")
    private String autoDevopsDeployStrategy;
    @JsonbProperty("repository_storage")
    private String repositoryStorage;
    @JsonbProperty("approvals_before_merge")
    private Integer approvalsBeforeMerge;
    @JsonbProperty("external_authorization_classification_label")
    private String externalAuthorizationClassificationLabel;
    @JsonbProperty("mirror")
    private Boolean mirror;
    @JsonbProperty("mirror_trigger_builds")
    private Boolean mirrorTriggerBuilds;
    @JsonbProperty("initialize_with_readme")
    private Boolean initializeWithReadme;
    @JsonbProperty("template_name")
    private String templateName;
    @JsonbProperty("template_project_id")
    private Integer templateProjectId;
    @JsonbProperty("use_custom_template")
    private Boolean useCustomTemplate;
    @JsonbProperty("group_with_project_templates_id")
    private Integer groupWithProjectTemplatesId;
    @JsonbProperty("packages_enabled")
    private Boolean packagesEnabled;
    @JsonbProperty("path_with_namespace")
    private String pathWithNamespace;
    private Namespace namespace;

    @JsonbTransient
    private boolean first;
    @JsonbTransient
    private boolean movedOrDeleted;

    public static Project from(ProjectSearchResults result) {
        return Project.builder().id(result.getId()).name(result.getName()).path(result.getPath())
                .namespaceId(result.getNamespace().getId()).build();
    }

    public void preserve() {
        if(tagList == null) {
            tagList = new ArrayList<>();
        }

        if(!tagList.contains(DO_NOT_DELETE)) {
            tagList.add(DO_NOT_DELETE);
        }
    }

}
