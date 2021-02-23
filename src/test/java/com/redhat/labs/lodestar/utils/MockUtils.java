package com.redhat.labs.lodestar.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.mockito.BDDMockito;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.redhat.labs.lodestar.config.JsonMarshaller;
import com.redhat.labs.lodestar.models.gitlab.Commit;
import com.redhat.labs.lodestar.models.gitlab.CommitMultiple;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.Namespace;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.models.gitlab.ProjectTreeNode;
import com.redhat.labs.lodestar.rest.client.GitLabService;

public class MockUtils {

    public static Integer REPO_ID = 2;
    public static Integer CUSTOMER_GROUP_ID = 2222;
    public static Integer PROJECT_GROUP_ID = 3333;
    public static Integer PROJECT_ID = 4444;

    public static Group mockRepositoryGroup() {
        return Group.builder().fullPath("top/dog").build();
    }

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

    public static ProjectTreeNode mockProjectTreeNode(String name) {
        return ProjectTreeNode.builder().name(name).build();
    }

    // get projects by group
    public static void setGetProjectsByGroupMock(GitLabService gitLabService, Integer projectId, List<Project> projects,
            boolean hasProject) {
        ResponseBuilder r = Response.ok(projects).header("X-Total-Pages", 1);
        if (hasProject) {
            r.entity(Lists.newArrayList(mockIacProject()));
            BDDMockito.given(gitLabService.getProjectsbyGroup(2, true, 100, 1)).willReturn(r.build());
        } else {
            BDDMockito.given(gitLabService.getProjectsbyGroup(Mockito.anyInt(), Mockito.anyBoolean(), Mockito.eq(100),
                    Mockito.eq(1))).willReturn(r.build());
        }
    }

    // get projects by id/path
    public static Integer setGetProjectByPathMock(GitLabService gitLabService, String path, boolean projectExists,
            Optional<Project> projectToReturn) {
        Project p = null;
        if (projectExists) {
            p = projectToReturn.orElse(Project.builder().id(path.length()).build());
        }
        BDDMockito.given(gitLabService.getProjectById(Mockito.eq(path))).willReturn(p);
        return path.length();
    }

    public static void setGetProjectByIdMock(GitLabService gitLabService, Integer id, boolean projectExists,
            Optional<Project> projectToReturn) {
        Project p = null;
        if (projectExists) {
            p = projectToReturn.orElse(Project.builder().id(id).build());
        }
        BDDMockito.given(gitLabService.getProjectById(Mockito.eq(String.valueOf(id)))).willReturn(p);
    }

    public static boolean isIdOrPathNumeric(String idOrPath) {
        try {
            Integer.valueOf(idOrPath);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    // get engagement file
    public static void setGetFileForEngagementJsonMock(GitLabService gitLabService, Integer projectId, boolean exists) {
        setGetFileMock(gitLabService, "engagement.json", String.valueOf(projectId), exists);
    }

    // get commits
    public static void setGetCommitLogMock(GitLabService gitLabService, Integer projectId,
            Integer expectedPagesReturned) {
        setGetCommitLogMock(gitLabService, String.valueOf(projectId), expectedPagesReturned);
    }

    public static void setGetCommitLogMock(GitLabService gitLabService, String projectIdOrPath,
            Integer expectedPagesReturned) {

        List<Commit> commitList = new ArrayList<Commit>();

        if (expectedPagesReturned > 0) {
            String content = ResourceLoader.load("commits.yaml");
            commitList = new JsonMarshaller().fromYaml(content, Commit.class);
        }

        BDDMockito.given(gitLabService.getCommitLog(projectIdOrPath, 100, 1))
                .willReturn(Response.ok(commitList).header("X-Total-Pages", expectedPagesReturned).build());

    }

    // get status file
    public static void setGetFileForStatusJsonMock(GitLabService gitLabService, Integer projectId, boolean exists) {
        setGetFileForStatusJsonMock(gitLabService, String.valueOf(projectId), exists);
    }

    public static void setGetFileForStatusJsonMock(GitLabService gitLabService, String path, boolean exists) {
        setGetFileMock(gitLabService, "status.json", path, exists);
    }

    public static void setGetFileMock(GitLabService gitLabService, String fileName, String projectIdOrPath,
            boolean exists) {

        File file = null;
        if (exists) {
            String content = ResourceLoader.load(fileName);
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            file = File.builder().filePath(fileName).content(content).build();
        }
        BDDMockito.given(gitLabService.getFile(projectIdOrPath, fileName, "master")).willReturn(file);

    }

    // get subgroups
    public static void setGetSubgroupsMock(GitLabService gitLabService, Optional<Integer> groupId,
            boolean hasSubgroups) {

        List<Group> groups = new ArrayList<Group>();
        // TODO: add more if required

        BDDMockito.given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.eq(100), Mockito.eq(1)))
                .willReturn(Response.ok(groups).header("X-Total-Pages", 1).build());

    }

    // create group
    public static void setCreateGroupMock(GitLabService gitLabService, Group customerGroup, Group projectGroup) {
        BDDMockito.given(gitLabService.createGroup(Mockito.any(Group.class))).willReturn(customerGroup, projectGroup);
    }

    // create project
    public static void setCreateProjectMock(GitLabService gitLabService, Project project) {
        BDDMockito.given(gitLabService.createProject(Mockito.any(Project.class))).willReturn(project);
    }

    // commit multiple files
    public static void setCommitMultipleFilesMock(GitLabService gitLabService, boolean succeed) {
        int statusCode = succeed ? 201 : 500;
        BDDMockito.given(gitLabService.commitMultipleFiles(Mockito.anyInt(), Mockito.any(CommitMultiple.class)))
                .willReturn(Response.status(statusCode).build());
    }

    // get group by id or path
    public static void setGetGroupByIdOrPathMock(GitLabService gitLabService, Integer groupId, Group group) {
        BDDMockito.given(gitLabService.getGroupByIdOrPath(String.valueOf(groupId))).willReturn(group);
    }

    public static void setGetGroupByIdOrPathMock(GitLabService gitLabService, String customerName, String projectName) {
        BDDMockito.given(gitLabService.getGroupByIdOrPath(Mockito.anyString())).willReturn(mockRepositoryGroup(),
                mockCustomerGroup(customerName), mockProjectGroup(projectName));
    }

    // get project hooks
    public static void setGetProjectHookMock(GitLabService gitLabService, Integer projectId) {

        List<Hook> hookList = new ArrayList<>();
        Hook hook = Hook.builder().id(13).url("http://webhook.edu/hook").token("token").projectId(projectId)
                .pushEvents(true).pushEventsBranchFilter("master").build();
        hookList.add(hook);
        BDDMockito.given(gitLabService.getProjectHooks(projectId)).willReturn(hookList);

    }

    // create project hooks
    public static void setCreateProjectHookMock(GitLabService gitLabService, Integer projectId) {
        BDDMockito.given(gitLabService.createProjectHook(Mockito.eq(projectId), Mockito.any(Hook.class)))
                .willReturn(Response.status(Status.CREATED).build());
    }
    
    public static void setDeleteGroupById(GitLabService gitLabService) {
        BDDMockito.doThrow(new WebApplicationException(404)).when(gitLabService).deleteGroupById(Mockito.anyInt());
    }

    public static void setDeleteProjectById(GitLabService gitLabService) {
        BDDMockito.doThrow(new WebApplicationException(404)).when(gitLabService).deleteProjectById(Mockito.anyInt());
    }

    public static void setProjectTreeNodeList(GitLabService gitLabService, String fileName) {

        ProjectTreeNode node1 = mockProjectTreeNode("another");
        ProjectTreeNode node2 = mockProjectTreeNode(fileName);
        Response response = Response.ok(Lists.newArrayList(node1, node2)).build();
        BDDMockito.given(gitLabService.getProjectTree(Mockito.anyString(), Mockito.anyBoolean())).willReturn(response);

    }

}
