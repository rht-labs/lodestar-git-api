package com.redhat.labs.lodestar.utils;

import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Namespace;
import com.redhat.labs.lodestar.models.gitlab.Project;

public class MockUtils {

    public static Integer REPO_ID = 2;
    public static Integer CUSTOMER_GROUP_ID = 2222;
    public static Integer PROJECT_GROUP_ID = 3333;
    public static Integer PROJECT_ID = 4444;

    public static Group mockCustomerGroup(String customerName) {
        return mockGroup(customerName, CUSTOMER_GROUP_ID, REPO_ID);
    }

    public static Group mockProjectGroup(String projectName) {
        return mockGroup(projectName, PROJECT_GROUP_ID, CUSTOMER_GROUP_ID);
    }

    public static Project mockIacProject() {
        return mockProject(PROJECT_ID, "iac", "private", mockNamespace(PROJECT_GROUP_ID, CUSTOMER_GROUP_ID));
    }

    public static Group mockGroup(String name, Integer groupId, Integer parentId) {
        return Group.builder().id(groupId).name(name).path(GitLabPathUtils.generateValidPath(name)).parentId(parentId)
                .build();
    }

    public static Project mockProject(Integer id, String name, String visibility, Namespace namespace) {
        return Project.builder().id(id).name(name).visibility(visibility).namespace(namespace).build();
    }

    public static Namespace mockNamespace(Integer id, Integer parentId) {
        return Namespace.builder().id(id).parentId(parentId).build();
    }

}
