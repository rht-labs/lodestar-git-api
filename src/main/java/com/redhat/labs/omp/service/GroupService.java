package com.redhat.labs.omp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.omp.models.gitlab.Group;
import com.redhat.labs.omp.rest.client.GitLabService;

@ApplicationScoped
public class GroupService {
    public static Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    @Inject
    @RestClient
    GitLabService gitLabService;

    // get a group
    public Optional<Group> getGitLabGroupByName(String name) throws UnexpectedGitLabResponseException {

        Optional<Group> optional = Optional.empty();

        List<Group> groupList = gitLabService.getGroupByName(name);

        if (null == groupList || groupList.isEmpty()) {
            return optional;
        }

        if (1 == groupList.size()) {
            return Optional.of(groupList.get(0));
        }

        // found more than one group with name in either 'name' or 'path' attribute
        // should match path
        for(Group group : groupList) {
            if(name.equalsIgnoreCase(group.getPath())) {
                return Optional.of(group);
            }
        }

        throw new UnexpectedGitLabResponseException(
                "No resource found with name equal to path attribute.");

    }

    public  List<Group> getAllGroups(Integer engagementRepositoryId) {

        //FIRST LEVEL
        List<Group> customerGroups = gitLabService.getSubGroups(engagementRepositoryId);

        List<Group> customerEngagementGroups = new ArrayList<>();
        customerGroups.stream().forEach(group -> customerEngagementGroups.addAll(gitLabService.getSubGroups(group.getId())));

        if(LOGGER.isDebugEnabled()) {
            customerEngagementGroups.stream().forEach(group -> LOGGER.debug("Group -> {}", group.getName()));
        }

        return customerEngagementGroups;
    }

    // create a group
    public Optional<Group> createGitLabGroup(Group group) {

        Optional<Group> optional = Optional.empty();

        // try to create the group
        Group createdGroup = gitLabService.createGroup(group);
        if (null != createdGroup) {
            optional = Optional.of(createdGroup);
        }

        return optional;

    }

    // update a group
    public Optional<Group> updateGitLabGroup(Integer groupId, Group group) {

        Optional<Group> optional = Optional.empty();

        // try to update the group
        Group updatedGroup = gitLabService.updateGroup(groupId, group);
        if (null != updatedGroup) {
            optional = Optional.of(updatedGroup);
        }

        return optional;

    }

    // remove a group
    public void deleteGroup(Integer groupId) {
        gitLabService.deleteGroupById(groupId);
    }

}
