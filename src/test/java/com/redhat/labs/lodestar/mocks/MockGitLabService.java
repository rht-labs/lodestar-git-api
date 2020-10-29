package com.redhat.labs.lodestar.mocks;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.redhat.labs.lodestar.config.JsonMarshaller;
import com.redhat.labs.lodestar.models.gitlab.Commit;
import com.redhat.labs.lodestar.models.gitlab.CommitMultiple;
import com.redhat.labs.lodestar.models.gitlab.DeployKey;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.Namespace;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.models.gitlab.ProjectSearchResults;
import com.redhat.labs.lodestar.models.gitlab.ProjectTransfer;
import com.redhat.labs.lodestar.rest.client.GitLabService;
import com.redhat.labs.lodestar.utils.EncodingUtils;
import com.redhat.labs.lodestar.utils.ResourceLoader;

import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
@RestClient
public class MockGitLabService implements GitLabService {

    @Override
    public Response getProjects() {
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
    public Response getGroupByName(String name, int pageSize, int page) {

        List<Group> groupList = new ArrayList<>();
        int pages = 0;

        if ("customer3".equalsIgnoreCase(name)) {
            groupList.add(Group.builder().id(3).name("customer1").path("customer1").build());
        } else if ("project1".equalsIgnoreCase(name)) {
            groupList.add(Group.builder().id(4).name("project1").path("project1").parentId(11).build());
        } else if ("customer".equalsIgnoreCase(name) || "customer A".equalsIgnoreCase(name)) {
            groupList.add(Group.builder().id(11).name("customer A").path("customer-a").parentId(2).build());
            groupList.add(Group.builder().id(12).name("customer").path("customer").parentId(10).build());
        } else if ("updated".equals(name)) {
            groupList.add(Group.builder().id(3).name("updated").path("updated").parentId(2).build());
        } else if ("updated2".equals(name)) {
            groupList.add(Group.builder().id(5).name("updated2").path("updated").parentId(3).build());
        }

        return Response.ok(groupList).header("X-Total-Pages", pages).build();
    }

    @Override
    public void deleteGroupById(Integer groupId) {
        // TODO Auto-generated method stub

    }

    @Override
    public Response getProjectByName(String name, int pageSize, int page) {
        
        List<ProjectSearchResults> results = new ArrayList<>();
        int pages = 0;
        
        if("iac".contentEquals(name)) {
            ProjectSearchResults project = ProjectSearchResults.builder().id(45).name("iac").description("bla").path("iac")
                    .namespace(Namespace.builder().id(45).build()).build();
            results.add(project);
            project = ProjectSearchResults.builder().id(5).name("iac").description("bla5").path("iac")
                    .namespace(Namespace.builder().id(5).build()).build();
            results.add(project);
            pages++;
        }

        return Response.ok(results).header("X-Total-Pages", pages).build();

    }

    @Override
    public Project getProjectById(String projectId) {

        if(projectId == "66") {
            return Project.builder().id(66).build();
        }
        
        if("top/dog/jello/lemon/iac".equals(projectId)) {
            return Project.builder().id(99).build();
        }
        
        if("top/dog/jello/tutti-frutti/iac".equals(projectId)) {
            return Project.builder().id(66).build();
        }
        return null;
    }

    @Override
    public Project createProject(Project project) {
        
        if("invalid".equals(project.getName())) {
            return null;
        }

        project.setId(45);

        return project;
    }

    @Override
    public Project updateProject(Integer projectId, Project project) {
        if(projectId == 45) {
            return new Project();
        }
        return null;
    }

    @Override
    public void deleteProjectById(Integer projectId) {
        // TODO Auto-generated method stub

    }

    @Override
    public File getFile(String projectId, String filePath, String ref) {

        if ("schema/meta.dat".equalsIgnoreCase(filePath)) {
            String content = "./residency.yml";
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();

        }
        
        if ("runtime/webhooks.yaml".equalsIgnoreCase(filePath)) {
            String content = ResourceLoader.load("webhooks.yaml");
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();

        }

        if ("schema/residency.yml".equalsIgnoreCase(filePath)) {
            String content = "---\n" + "\n" + "residency:\n" + "  id: \"{engagement.id}\"\n"
                    + "  customer_name: \"{engagement.customerName}\"\n"
                    + "  project_name: \"{engagement.projectName}\"\n";
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();
        } 
        
        if("engagement.json".equals(filePath)) {
            String content = ResourceLoader.load("engagement.json");
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();
        }
        
        if("400.error".equals(filePath)) {
            throw new WebApplicationException(404);
        }
        
        if("500.error".equals(filePath)) {
            throw new WebApplicationException(500);
        }

        if("runtime/lodestar-runtime-config.yaml".equals(filePath)) {
            String content = ResourceLoader.load("lodestar-runtime-config.yaml");
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();
        }
        
        if("schema/webhooks.json".equals(filePath)) {
            String content = ResourceLoader.load("webhooks.json");
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();
        }
        
        if("status.json".equals(filePath) && ! projectId.equals("top/dog/nope/nada/iac")) {
            String content = ResourceLoader.load("status.json");
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();
        }

        return null;
    }

    @Override
    public File createFile(Integer projectId, String filePath, File file) {
        if(filePath.equals("create.file")) {
            return file;
        }
        return null;
    }

    @Override
    public File updateFile(Integer projectId, String filePath, File file) {
        if("update.file".equals(filePath)) {
            String content = ResourceLoader.load("engagement.json");
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            return File.builder().filePath(filePath).content(content).build();
        }
        
        return null;
    }

    @Override
    public void deleteFile(Integer projectId, String filePath, File file) {
        // TODO Auto-generated method stub

    }

    @Override
    public Response commitMultipleFiles(Integer projectId, CommitMultiple commit) {
        if("fail@commitmultiplefiles.com".equals(commit.getAuthorEmail())) {
            return Response.ok().build();
        }
        return Response.status(201).build();
    }

    @Override
    public Response enableDeployKey(Integer projectId, Integer deployKey) {
        return Response.ok().build();
    }

    @Override
    public Response getProjectsbyGroup(Integer groupId, Boolean includeSubgroups, int page, int pageSize) {
        List<Project> projects = new ArrayList<>();
        projects.add(Project.builder().id(groupId * 10).name("Project " + (groupId*10)).build());
        return Response.ok(projects).header("X-Total-Pages", 1).build();
    }

    @Override
    public Response getSubGroups(Integer groupId, Integer perPage, Integer page) {
        List<Group> groups = new ArrayList<>();
        groups.add(Group.builder().id(groupId + 1).name("Group 1").build());
        return Response.ok(groups).build();
    }

    @Override
    public Response getFileWithResponse(Integer projectId, String filePath, String ref) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response createProjectHook(Integer projectId, Hook hook) {
        if(projectId == 66) {
            return Response.status(Status.CREATED).build();
        }
        
        return null;
    }

    @Override
    public Response updateProjectHook(Integer projectId, Integer hookId, Hook hook) {
        if(projectId == 99) {
            return Response.ok().build();
        } 
        return null;
    }

    @Override
    public List<Hook> getProjectHooks(Integer projectId) {
        List<Hook> hookList = new ArrayList<>();
        
        if(projectId == 99) {
            Hook hook = Hook.builder().id(13).url("http://webhook.edu/hook").token("token").projectId(99)
                    .pushEvents(true).pushEventsBranchFilter("master").build();
            hookList.add(hook);
        }
        return hookList;
    }

    @Override
    public Group getGroupByIdOrPath(String idOrPath) {
        if("2".equals(idOrPath)) {
            return Group.builder().fullPath("top/dog").build();
        }
        return null;
    }

    @Override
    public Response updateDeployKey(Integer projectId, Integer deployKeyId, DeployKey deployKey) {
        return null;
    }

    @Override
    public Response getCommitLog(String projectId, int perPage, int pageNumber) {
        String content = ResourceLoader.load("commits.yaml");
        List<Commit> commitList = new JsonMarshaller().fromYaml(content, Commit.class);
        
        if("top/dog/jello/lemon/iac".equals(projectId)) {
            return Response.ok(commitList).header("X-Total-Pages", 1).build();
        }
        
        if("multi/page/iac".equals(projectId)) {
            return Response.ok(commitList).header("X-Total-Pages", 2).build();
        }
        
        return Response.ok(new ArrayList<Commit>()).header("X-Total-Pages", 0).build();
    }

    @Override
    public Optional<Project> transferProject(Integer projectId, ProjectTransfer projectTransfer) {
        // TODO Auto-generated method stub
        return null;
    }

}
