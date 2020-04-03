package com.redhat.labs.omp.mocks;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.redhat.labs.omp.models.gitlab.CommitMultiple;
import com.redhat.labs.omp.models.gitlab.File;
import com.redhat.labs.omp.models.gitlab.Group;
import com.redhat.labs.omp.models.gitlab.Project;
import com.redhat.labs.omp.models.gitlab.ProjectSearchResults;
import com.redhat.labs.omp.rest.client.GitLabService;
import com.redhat.labs.omp.utils.EncodingUtils;

import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
@RestClient
public class MockGitLabService implements GitLabService {

    @Override
    public Response getProjects() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Group createGroup(Group group) {

        if ("customer1".equalsIgnoreCase(group.getName())) {
            return Group.builder().id(1).name("customer1").path("customer1").build();
        } else if ("project1".equalsIgnoreCase(group.getName())) {
            return Group.builder().id(2).name("project1").path("project1").build();
        }

        return null;

    }

    @Override
    public Group updateGroup(Integer groupId, Group group) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Group> getGroupByName(String name) {

        List<Group> groupList = new ArrayList<>();

        if ("customer3".equalsIgnoreCase(name)) {
            groupList.add(Group.builder().id(3).name("customer1").path("customer1").build());
        } else if ("project4".equalsIgnoreCase(name)) {
            groupList.add(Group.builder().id(4).name("project1").path("project1").build());
        }

        return groupList;
    }

    @Override
    public void deleteGroupById(Integer groupId) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<ProjectSearchResults> getProjectByName(String name) {

        List<ProjectSearchResults> results = new ArrayList<>();

//        results.add(
//                ProjectSearchResults.builder()
//                    .id(3)
//                    .name("iac")
//                    .path("iac")
//                    .description("iac for project1")
//                    .namespace(Namespace
//                            .builder()
//                            .parentId(2)
//                            .build())
//                    .build());

        return results;

    }

    @Override
    public Project getProjectById(Integer projectId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Project createProject(Project project) {

        project.setId(45);

        return project;
    }

    @Override
    public Project updateProject(Integer projectId, Project project) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteProjectById(Integer projectId) {
        // TODO Auto-generated method stub

    }

    @Override
    public File getFile(Integer projectId, String filePath, String ref) {

        if ("schema/meta.dat".equalsIgnoreCase(filePath)) {

            String content = "./residency.yml";
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();

        } else if ("schema/residency.yml".equalsIgnoreCase(filePath)) {
            String content = "---\n" + "\n" + "residency:\n" + "  id: \"{engagement.id}\"\n"
                    + "  customer_name: \"{engagement.customerName}\"\n"
                    + "  project_name: \"{engagement.projectName}\"\n";
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();
        }

        return null;
    }

    @Override
    public File createFile(Integer projectId, String filePath, File file) {
        return file;
    }

    @Override
    public File updateFile(Integer projectId, String filePath, File file) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteFile(Integer projectId, String filePath, File file) {
        // TODO Auto-generated method stub

    }

    @Override
    public Response commitMultipleFiles(Integer projectId, CommitMultiple commit) {
        // TODO: need to be able to have negative scenarios
        return Response.status(201).build();
    }

    @Override
    public Response enableDeployKey(Integer projectId, Integer deployKey) {
        return Response.ok().build();
    }

}
