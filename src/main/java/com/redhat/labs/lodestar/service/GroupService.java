package com.redhat.labs.lodestar.service;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.labs.lodestar.exception.UnexpectedGitLabResponseException;
import com.redhat.labs.lodestar.models.PagedResults;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.rest.client.GitLabService;

@ApplicationScoped
public class GroupService {
    public static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    @Inject
    @RestClient
    GitLabService gitLabService;
    
    @ConfigProperty(name = "commit.page.size")
    int commitPageSize;

    // get a group
    public Optional<Group> getGitLabGroupByName(String name, Integer parentId)
            throws UnexpectedGitLabResponseException {

        Optional<Group> optional = Optional.empty();
        
        PagedResults<Group> page = new PagedResults<>();
                
        while(page.hasMore()) {
            Response response = gitLabService.getGroupByName(name, commitPageSize, page.getNumber());
            page.update(response, new GenericType<List<Group>>() {});
        }

        // look for a match between returned name and provided path
        for (Group group : page.getResults()) {
            if (name.equals(group.getName()) && parentId.equals(group.getParentId())) {
                return Optional.of(group);
            }
        }

        return optional;

    }

    public List<Group> getSubgroups(Integer groupId) {

        PagedResults<Group> page = new PagedResults<>();

        while(page.hasMore()) {
            Response response = gitLabService.getSubGroups(groupId, commitPageSize, page.getNumber());
            page.update(response, new GenericType<List<Group>>() {});
        }

        return page.getResults();

    }

    public Optional<Group> getGitLabGroupByById(int id) {
        return getGitLabGroupByByIdOrPath(String.valueOf(id));
    }
    
    public Optional<Group> getGitLabGroupByByIdOrPath(String idOrPath) {
        
        Group group = gitLabService.getGroupByIdOrPath(idOrPath);
        return Optional.ofNullable(group);
    }


    // create a group
    public Optional<Group> createGitLabGroup(Group group) {

        // try to create the group
        Group createdGroup = gitLabService.createGroup(group);
        return Optional.ofNullable(createdGroup);

    }

    // update a group
    public Optional<Group> updateGitLabGroup(Integer groupId, Group group) {

        // try to update the group
        Group updatedGroup = gitLabService.updateGroup(groupId, group);
        return Optional.ofNullable(updatedGroup);

    }

    // remove a group
    public void deleteGroup(Integer groupId) {
        gitLabService.deleteGroupById(groupId);
    }

}
