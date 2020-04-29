package com.redhat.labs.omp.service;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.redhat.labs.omp.models.gitlab.Group;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GroupServiceTest {

    @Inject
    GroupService groupService;
    
    @Test
    public void testGetGitLabGroupByNameNoGroupsExist() {

        Optional<Group> optional = groupService.getGitLabGroupByName("customerA", 1);
        Assertions.assertFalse(optional.isPresent());

    }

    @Test
    public void testGetGitLabGroupByNameMultipleGroupsExistNoMatch() {

        Optional<Group> optional = groupService.getGitLabGroupByName("customer", 1);
        Assertions.assertFalse(optional.isPresent());

    }

    @Test
    public void testGetGitLabGroupByNameMultipleGroupsExistMatch() {

        Optional<Group> optional = groupService.getGitLabGroupByName("customer", 10);
        Assertions.assertTrue(optional.isPresent());

    }

}
