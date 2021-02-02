package com.redhat.labs.lodestar.service;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.redhat.labs.lodestar.models.gitlab.Group;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class GroupServiceTest {

    @Inject
    GroupService groupService;
    
    @Test
    void testGetGitLabGroupByNameNoGroupsExist() {

        Optional<Group> optional = groupService.getGitLabGroupByName("customerA", 1);
        Assertions.assertFalse(optional.isPresent());

    }

    @Test
    void testGetGitLabGroupByNameMultipleGroupsExistNoMatch() {

        Optional<Group> optional = groupService.getGitLabGroupByName("customer", 1);
        Assertions.assertFalse(optional.isPresent());

    }

    @Test
    void testGetGitLabGroupByNameMultipleGroupsExistMatch() {

        Optional<Group> optional = groupService.getGitLabGroupByName("customer", 10);
        Assertions.assertTrue(optional.isPresent());

    }

}
