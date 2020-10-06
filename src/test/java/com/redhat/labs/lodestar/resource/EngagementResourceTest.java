package com.redhat.labs.lodestar.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import com.redhat.labs.lodestar.config.JsonMarshaller;
import com.redhat.labs.lodestar.models.gitlab.Commit;
import com.redhat.labs.lodestar.models.gitlab.CommitMultiple;
import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Hook;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.rest.client.GitLabService;
import com.redhat.labs.lodestar.utils.EncodingUtils;
import com.redhat.labs.lodestar.utils.MockUtils;
import com.redhat.labs.lodestar.utils.ResourceLoader;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;

@QuarkusTest
class EngagementResourceTest {

    @InjectMock
    @RestClient
    GitLabService gitLabService;

    @BeforeEach
    void setup() {

        // set the engagement path prefix
        Group g = Group.builder().fullPath("top/dog").build();
        BDDMockito.given(gitLabService.getGroupByIdOrPath("2")).willReturn(g);

    }

    @Test
    void testGetAllEngagementsSuccess() {

        // get engagements by group
        List<Project> projects = new ArrayList<>();
        projects.add(Project.builder().id(20).name("Project " + (20)).build());
        setGetProjectsByGroupMock(20, projects);

        // get engagement file
        setGetFileForEngagementJsonMock(20, true);

        // get commits
        setGetCommitLogMock(0, 0);


        // get status file
        setGetFileForStatusJsonMock(20, true);

        given()
            .when()
                .contentType(ContentType.JSON)
                .get("/api/v1/engagements")
            .then()
                .statusCode(200)
                .body(is("[{\"archive_date\":\"20210125\",\"commits\":[],\"customer_contact_email\":\"reg@chiefs.com\",\"customer_contact_name\":\"Reg Dunlop\",\"customer_name\":\"customer1\","
                        + "\"description\":\"Charleston\",\"end_date\":\"20201225\",\"engagement_lead_email\":\"doug93@leafs.com\",\"engagement_lead_name\":\"Doug Gilmour\",\"location\":\"Raleigh, NC\","
                        + "\"ocp_cloud_provider_name\":\"GCP\",\"ocp_cloud_provider_region\":\"West\",\"ocp_cluster_size\":\"medium\",\"ocp_persistent_storage_size\":\"50GB\",\"ocp_sub_domain\":\"jello\","
                        + "\"ocp_version\":\"v4.2\",\"project_id\":0,\"project_name\":\"project1\",\"public_reference\":false,\"start_date\":\"20200202\",\"status\":{\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\","
                        + "\"updated\":\"2020-06-23T21:25:31Z\"},{\"message\":\"This is message 2\",\"severity\":\"CRITICAL\",\"updated\":\"2020-06-22T11:15:11Z\"},{\"message\":\"This is message 3\","
                        + "\"severity\":\"SUCCESS\",\"updated\":\"2020-06-22T10:25:31Z\"}],\"overall_status\":\"yellow\",\"subsystems\":[{\"access_urls\":[{\"Web Console\":\"https://console......\"},"
                        + "{\"API\":\"https://api.....:6443\"}],\"info\":\"Deployment In Progress\",\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],"
                        + "\"name\":\"openshift\",\"state\":\"provisioning\",\"status\":\"yellow\",\"updated\":\"2020-06-23T09:25:31Z\"},{\"access_urls\":[{\"atlassian\":\"https://mycompany.atlassian.net\"}],"
                        + "\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"atlassian\",\"state\":\"operational\",\"status\":\"green\","
                        + "\"updated\":\"2020-06-22T20:15:37Z\"}]},\"technical_lead_email\":\"wendel17@leafs.com\",\"technical_lead_name\":\"Wendel Clark\"}]"));

    }
    
    @Test
    void tesetGetEngagementByNamespace() {

        // get projects by id
        Integer projectId = setGetProjectByPathMock("top/dog/jello/tutti-frutti/iac", true, Optional.empty());

        // get engagement json
        setGetFileForEngagementJsonMock(projectId, true);

        // get commit log
        setGetCommitLogMock(0, 0);

        given()
            .pathParam("namespace", "top/dog/jello/tutti-frutti/iac")
            .when()
                .contentType(ContentType.JSON)
                .get("/api/v1/engagements/namespace/{namespace}")
            .then()
                .statusCode(200)
                .body(is("{\"archive_date\":\"20210125\",\"commits\":[],\"customer_contact_email\":\"reg@chiefs.com\",\"customer_contact_name\":\"Reg Dunlop\",\"customer_name\":\"customer1\","
                        + "\"description\":\"Charleston\",\"end_date\":\"20201225\",\"engagement_lead_email\":\"doug93@leafs.com\",\"engagement_lead_name\":\"Doug Gilmour\",\"location\":\"Raleigh, NC\","
                        + "\"ocp_cloud_provider_name\":\"GCP\",\"ocp_cloud_provider_region\":\"West\",\"ocp_cluster_size\":\"medium\",\"ocp_persistent_storage_size\":\"50GB\",\"ocp_sub_domain\":\"jello\","
                        + "\"ocp_version\":\"v4.2\",\"project_id\":0,\"project_name\":\"project1\",\"public_reference\":false,\"start_date\":\"20200202\",\"technical_lead_email\":\"wendel17@leafs.com\",\"technical_lead_name\":\"Wendel Clark\"}"));
    }

    @Test
    void testCreateEngagementSuccess() {

        Group customerGroup = MockUtils.mockCustomerGroup("new");
        Group projectGroup = MockUtils.mockProjectGroup("new2");
        Project iacProject = MockUtils.mockIacProject();

        setGetProjectByIdMock(null, false, Optional.empty());
        setGetSubgroupsMock(Optional.empty(), false);
        setCreateGroupMock(customerGroup, projectGroup);
        setCreateProjectMock(iacProject);
        setCommitMultipleFilesMock(true);
        setGetFileMock("runtime/webhooks.yaml", String.valueOf(iacProject.getId()), false);

        given()
            .when()
                .contentType(ContentType.JSON)
                .queryParam("username", "jdoe")
                .queryParam("userEmail", "jdoe@email.com")
                .body(ResourceLoader.load("engagement.json"))
                .post("/api/v1/engagements")
            .then()
                .statusCode(201);
    }

    @Test
    void testUpdateEngagementSuccess() {

        Group customerGroup = MockUtils.mockCustomerGroup("customer A");
        Group projectGroup = MockUtils.mockProjectGroup("project1");
        Project iacProject = MockUtils.mockIacProject();

        setGetProjectByIdMock(iacProject.getId(), true, Optional.of(iacProject));
        setGetGroupByIdOrPathMock(customerGroup.getId(), customerGroup);
        setGetGroupByIdOrPathMock(projectGroup.getId(), projectGroup);
        setGetSubgroupsMock(Optional.empty(), false);
        setCommitMultipleFilesMock(true);
        setGetFileMock("runtime/webhooks.yaml", String.valueOf(iacProject.getId()), false);

        given()
            .when()
                .contentType(ContentType.JSON)
                .queryParam("username", "jdoe")
                .queryParam("userEmail", "jdoe@email.com")
                .body(ResourceLoader.load("engagement-update.json"))
                .post("/api/v1/engagements")
            .then()
                .statusCode(201);
    }

    @Test
    void testGetWebhooksSuccess() {

        Project p = Project.builder().id(99).build();
        setGetProjectByPathMock("top/dog/jello/lemon/iac", true, Optional.of(p));
        setGetProjectHookMock(99);

        given()
            .when()
                .contentType(ContentType.JSON)
                .get("/api/v1/engagements/customer/jello/lemon/hooks")
            .then()
                .statusCode(200)
                .body(is("[{\"id\":13,\"project_id\":99,\"push_events\":true,\"push_events_branch_filter\""
                        + ":\"master\",\"token\":\"token\",\"url\":\"http://webhook.edu/hook\"}]"));
    }
    
    @Test
    void testCreateProjectHookSuccess() {

        // Get Project
        Project p = Project.builder().id(66).build();
        setGetProjectByPathMock("top/dog/jello/tutti-frutti/iac", true, Optional.of(p));
        setCreateProjectHookMock(66);

        given()
            .when()
                .contentType(ContentType.JSON)
                .body("{\"push_events\": true, \"url\": \"https://lodestar/webhooks/blah\"}")
                .post("/api/v1/engagements/customer/jello/tutti-frutti/hooks")
            .then()
                .statusCode(201);
    }
    
    @Test
    void testCreateProjectHookFailAlreadyExists() {
        given()
            .when()
                .contentType(ContentType.JSON)
                .body("{\"push_events\": true, \"url\": \"http://webhook.edu/hook\"}")
                .post("/api/v1/engagements/customer/jello/lemon/hooks")
            .then()
                .statusCode(400);
    }
    
    @Test
    void testCreateProjectHookFailNoProject() {
        given()
            .when()
                .contentType(ContentType.JSON)
                .body("{\"push_events\": true, \"url\": \"https://lodestar/webhooks/blah\"}")
                .post("/api/v1/engagements/customer/nope/tutti-frutti/hooks")
            .then()
                .statusCode(400);
    }

    @Test
    void testGetStatusSuccess() {

        setGetFileForStatusJsonMock("top/dog/jello/lemon/iac", true);

        given()
            .when()
                .contentType(ContentType.JSON)
                .get("/api/v1/engagements/customer/jello/lemon/status")
            .then()
                .statusCode(200)
                .body(is("{\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"},{\"message\":\"This is message 2\",\"severity\":\"CRITICAL\",\"updated\":\"2020-06-22T11:15:11Z\"},"
                        + "{\"message\":\"This is message 3\",\"severity\":\"SUCCESS\",\"updated\":\"2020-06-22T10:25:31Z\"}],\"overall_status\":\"yellow\",\"subsystems\":[{\"access_urls\":[{\"Web Console\":\"https://console......\"},"
                        + "{\"API\":\"https://api.....:6443\"}],\"info\":\"Deployment In Progress\",\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"openshift\","
                        + "\"state\":\"provisioning\",\"status\":\"yellow\",\"updated\":\"2020-06-23T09:25:31Z\"},{\"access_urls\":[{\"atlassian\":\"https://mycompany.atlassian.net\"}],\"messages\":[{\"message\":\"This is message 1\","
                        + "\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"atlassian\",\"state\":\"operational\",\"status\":\"green\",\"updated\":\"2020-06-22T20:15:37Z\"}]}"));
    }
    
    @Test
    void testGetProjectSuccess() {

        Project p = Project.builder().id(20).name("Project " + (20)).build();
        setGetProjectByPathMock("top/dog/jello/lemon/iac", true, Optional.of(p));
        setGetFileForEngagementJsonMock(20, true);
        setGetCommitLogMock(0, 0);
        setGetFileForStatusJsonMock(20, true);

        given()
            .when()
                .contentType(ContentType.JSON)
                .queryParam("includeStatus", true)
                .get("/api/v1/engagements/customer/jello/lemon")
            .then()
                .statusCode(200)
                .body(is("{\"archive_date\":\"20210125\",\"commits\":[],\"customer_contact_email\":\"reg@chiefs.com\",\"customer_contact_name\":\"Reg Dunlop\",\"customer_name\":\"customer1\",\"description\":\"Charleston\",\"end_date\":\"20201225\","
                        + "\"engagement_lead_email\":\"doug93@leafs.com\",\"engagement_lead_name\":\"Doug Gilmour\",\"location\":\"Raleigh, NC\",\"ocp_cloud_provider_name\":\"GCP\",\"ocp_cloud_provider_region\":\"West\",\"ocp_cluster_size\":\"medium\","
                        + "\"ocp_persistent_storage_size\":\"50GB\",\"ocp_sub_domain\":\"jello\",\"ocp_version\":\"v4.2\",\"project_id\":0,\"project_name\":\"project1\",\"public_reference\":false,\"start_date\":\"20200202\",\"status\":{\"messages\":[{\"message\":\"This is message 1\","
                        + "\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"},{\"message\":\"This is message 2\",\"severity\":\"CRITICAL\",\"updated\":\"2020-06-22T11:15:11Z\"},{\"message\":\"This is message 3\",\"severity\":\"SUCCESS\","
                        + "\"updated\":\"2020-06-22T10:25:31Z\"}],\"overall_status\":\"yellow\",\"subsystems\":[{\"access_urls\":[{\"Web Console\":\"https://console......\"},{\"API\":\"https://api.....:6443\"}],\"info\":\"Deployment In Progress\","
                        + "\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"openshift\",\"state\":\"provisioning\",\"status\":\"yellow\",\"updated\":\"2020-06-23T09:25:31Z\"},"
                        + "{\"access_urls\":[{\"atlassian\":\"https://mycompany.atlassian.net\"}],\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"atlassian\",\"state\":\"operational\","
                        + "\"status\":\"green\",\"updated\":\"2020-06-22T20:15:37Z\"}]},\"technical_lead_email\":\"wendel17@leafs.com\",\"technical_lead_name\":\"Wendel Clark\"}"));
    }
    
    @Test
    void testGetCommitsSuccess() {

        setGetCommitLogMock("top/dog/jello/lemon/iac", 1);

        given()
            .when()
                .contentType(ContentType.JSON)
                .get("/api/v1/engagements/customer/jello/lemon/commits")
            .then()
                .statusCode(200)
                .body(is("[{\"author_email\":\"bot@bot.com\",\"author_name\":\"bot\",\"authored_date\":\"2020-06-16T00:12:27.000+00:00\",\"committed_date\":\"2020-06-16T00:12:27.000+00:00\",\"id\":\"551eefc6e367aa2ad3c56bdd7229c7bc525d4f0c\","
                        + "\"message\":\"Auto-update generated files\",\"short_id\":\"551eefc6\",\"title\":\"Auto-update generated files\",\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/551eefc6e367aa2ad3c56bdd7229c7bc525d4f0c\"},"
                        + "{\"author_email\":\"mmarner@example.com\",\"author_name\":\"Mitch Marner\",\"authored_date\":\"2020-06-16T00:12:18.000+00:00\",\"committed_date\":\"2020-06-16T00:12:18.000+00:00\","
                        + "\"id\":\"5178ffab3566ac591af95c3383d1c5916de4a3a9\",\"message\":\"Update engagement.json\",\"short_id\":\"5178ffab\",\"title\":\"Update engagement.json\","
                        + "\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/5178ffab3566ac591af95c3383d1c5916de4a3a9\"},{\"author_email\":\"jtavares@example.com\",\"author_name\":\"John Tavares\","
                        + "\"authored_date\":\"2020-06-11T16:46:19.000+00:00\",\"committed_date\":\"2020-06-11T16:46:19.000+00:00\",\"id\":\"7865570dc63b1463d9fb4d02bd09ff46d244e694\",\"message\":\"Update status.json\","
                        + "\"short_id\":\"7865570d\",\"title\":\"Update status.json\",\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/7865570dc63b1463d9fb4d02bd09ff46d244e694\"},{\"author_email\":\"mmarner@example.com\","
                        + "\"author_name\":\"Mitch Marner\",\"authored_date\":\"2020-06-04T22:34:10.000+00:00\",\"committed_date\":\"2020-06-04T22:34:10.000+00:00\",\"id\":\"dd0cc0fa7868210e2eb5a030f07cc0221dd6bc9f\","
                        + "\"message\":\"Bump OCP version (jacob test)\",\"short_id\":\"dd0cc0fa\",\"title\":\"Bump OCP version (test)\",\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/dd0cc0fa7868210e2eb5a030f07cc0221dd6bc9f\"}]"));
    }

    ///////////////////////
    
    // get projects by group
    void setGetProjectsByGroupMock(Integer projectId, List<Project> projects) {
        Response r = Response.ok(projects).header("X-Total-Pages", 1).build();
        BDDMockito.given(gitLabService.getProjectsbyGroup(2, true, 100, 1)).willReturn(r);
    }

    // get projects by id/path
    Integer setGetProjectByPathMock(String path, boolean projectExists, Optional<Project> projectToReturn) {
        Project p = null;
        if(projectExists) {
            p = projectToReturn.orElse(Project.builder().id(path.length()).build());
        }
        BDDMockito.given(gitLabService.getProjectById(Mockito.eq(path))).willReturn(p);
        return path.length();
    }

    void setGetProjectByIdMock(Integer id, boolean projectExists, Optional<Project> projectToReturn) {
        Project p = null;
        if(projectExists) {
            p = projectToReturn .orElse(Project.builder().id(id).build());
        }
        BDDMockito.given(gitLabService.getProjectById(Mockito.eq(String.valueOf(id)))).willReturn(p);
    }

    boolean isIdOrPathNumeric(String idOrPath) {
        try {
            Integer.valueOf(idOrPath);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }

    }

    // get engagement file
    void setGetFileForEngagementJsonMock(Integer projectId, boolean exists) {
        setGetFileMock("engagement.json", String.valueOf(projectId), exists);
    }

    // get commits
    void setGetCommitLogMock(Integer projectId, Integer expectedPagesReturned) {
        setGetCommitLogMock(String.valueOf(projectId), expectedPagesReturned);
    }

    void setGetCommitLogMock(String projectIdOrPath, Integer expectedPagesReturned) {

        List<Commit> commitList = new ArrayList<Commit>();

        if (expectedPagesReturned > 0) {
            String content = ResourceLoader.load("commits.yaml");
            commitList = new JsonMarshaller().fromYaml(content, Commit.class);
        }

        BDDMockito.given(gitLabService.getCommitLog(projectIdOrPath, 100, 1))
            .willReturn(Response.ok(commitList).header("X-Total-Pages", expectedPagesReturned).build());

    }

    // get status file
    void setGetFileForStatusJsonMock(Integer projectId, boolean exists) {
        setGetFileForStatusJsonMock(String.valueOf(projectId), exists);
    }

    void setGetFileForStatusJsonMock(String path, boolean exists) {
        setGetFileMock("status.json", path, exists);
    }

    void setGetFileMock(String fileName, String projectIdOrPath, boolean exists) {

        File file = null;
        if (exists) {
            String content = ResourceLoader.load(fileName);
            content = new String(EncodingUtils.base64Encode(content.getBytes()), StandardCharsets.UTF_8);
            file = File.builder().filePath(fileName).content(content).build();
        }
        BDDMockito.given(gitLabService.getFile(projectIdOrPath, fileName, "master")).willReturn(file);

    }

    // get subgroups
    void setGetSubgroupsMock(Optional<Integer> groupId, boolean hasSubgroups) {

        List<Group> groups = new ArrayList<Group>();
        // TODO:  add more if required

        BDDMockito.given(gitLabService.getSubGroups(Mockito.anyInt(), Mockito.eq(100), Mockito.eq(1)))
            .willReturn(Response.ok(groups).header("X-Total-Pages", 1).build());

    }

    // create group
    void setCreateGroupMock(Group customerGroup, Group projectGroup) {
        BDDMockito.given(gitLabService.createGroup(Mockito.any(Group.class))).willReturn(customerGroup, projectGroup);
    }

    // create project
    void setCreateProjectMock(Project project) {
        BDDMockito.given(gitLabService.createProject(Mockito.any(Project.class))).willReturn(project);
    }

    // commit multiple files
    void setCommitMultipleFilesMock(boolean succeed) {
        int statusCode = succeed ? 201 : 500;
        BDDMockito.given(gitLabService.commitMultipleFiles(Mockito.anyInt(), Mockito.any(CommitMultiple.class)))
            .willReturn(Response.status(statusCode).build());
    }

    // get group by id or path
    void setGetGroupByIdOrPathMock(Integer groupId, Group group) {
        BDDMockito.given(gitLabService.getGroupByIdOrPath(String.valueOf(groupId))).willReturn(group);
    }

    // get project hooks
    void setGetProjectHookMock(Integer projectId) {

        List<Hook> hookList = new ArrayList<>();
        Hook hook = Hook.builder().id(13).url("http://webhook.edu/hook").token("token").projectId(projectId)
                    .pushEvents(true).pushEventsBranchFilter("master").build();
        hookList.add(hook);
        BDDMockito.given(gitLabService.getProjectHooks(projectId)).willReturn(hookList);

    }

    // create project hooks
    void setCreateProjectHookMock(Integer projectId) {
        BDDMockito.given(gitLabService.createProjectHook(Mockito.eq(projectId), Mockito.any(Hook.class)))
            .willReturn(Response.status(Status.CREATED).build());
    }

}
