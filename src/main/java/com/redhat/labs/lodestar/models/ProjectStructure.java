package com.redhat.labs.lodestar.models;

import java.util.Optional;

import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStructure {

    @Builder.Default
    private Optional<Integer> customerGroupId = Optional.empty();
    @Builder.Default
    private Optional<Group> customerGroup = Optional.empty();
    @Builder.Default
    private Boolean customerGroupHasSubgroups = Boolean.FALSE;

    @Builder.Default
    private Optional<Integer> projectGroupId = Optional.empty();
    @Builder.Default
    private Optional<Group> projectGroup = Optional.empty();

    @Builder.Default
    private Optional<Project> project = Optional.empty();

}