package com.redhat.labs.lodestar.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.redhat.labs.lodestar.models.gitlab.Group;
import com.redhat.labs.lodestar.models.gitlab.Project;
import com.redhat.labs.lodestar.rest.client.GitLabService;
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
    MockUtils.setGetGroupByIdOrPathMock(gitLabService, 2, g);

  }

  @Test
  void testGetAllEngagementsSuccess() {

    // get engagements by group
    List<Project> projects = new ArrayList<>();
    projects.add(Project.builder().id(20).name("Project " + (20)).build());
    MockUtils.setGetProjectsByGroupMock(gitLabService, 20, projects);

    // get engagement file
    MockUtils.setGetFileForEngagementJsonMock(gitLabService, 20, true);

    // get commits
    MockUtils.setGetCommitLogMock(gitLabService, 0, 0);

    // get status file
    MockUtils.setGetFileForStatusJsonMock(gitLabService, 20, true);

    given().when().contentType(ContentType.JSON).get("/api/v1/engagements").then().statusCode(200).body(is(
        "[{\"archive_date\":\"20210125\",\"commits\":[],\"customer_contact_email\":\"reg@chiefs.com\",\"customer_contact_name\":\"Reg Dunlop\",\"customer_name\":\"customer1\",\"description\":\"Charleston\",\"end_date\":\"20201225\",\"engagement_lead_email\":\"doug93@leafs.com\",\"engagement_lead_name\":\"Doug Gilmour\",\"location\":\"Raleigh, NC\",\"project_id\":0,\"project_name\":\"project1\",\"public_reference\":false,\"start_date\":\"20200202\",\"status\":{\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"},{\"message\":\"This is message 2\",\"severity\":\"CRITICAL\",\"updated\":\"2020-06-22T11:15:11Z\"},{\"message\":\"This is message 3\",\"severity\":\"SUCCESS\",\"updated\":\"2020-06-22T10:25:31Z\"}],\"overall_status\":\"yellow\",\"subsystems\":[{\"access_urls\":[{\"Web Console\":\"https://console......\"},{\"API\":\"https://api.....:6443\"}],\"info\":\"Deployment In Progress\",\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"openshift\",\"state\":\"provisioning\",\"status\":\"yellow\",\"updated\":\"2020-06-23T09:25:31Z\"},{\"access_urls\":[{\"atlassian\":\"https://mycompany.atlassian.net\"}],\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"atlassian\",\"state\":\"operational\",\"status\":\"green\",\"updated\":\"2020-06-22T20:15:37Z\"}]},\"technical_lead_email\":\"wendel17@leafs.com\",\"technical_lead_name\":\"Wendel Clark\"}]"));

  }

  @Test
  void tesetGetEngagementByNamespace() {

    // get projects by id
    Integer projectId = MockUtils.setGetProjectByPathMock(gitLabService, "top/dog/jello/tutti-frutti/iac", true,
        Optional.empty());

    // get engagement json
    MockUtils.setGetFileForEngagementJsonMock(gitLabService, projectId, true);

    // get commit log
    MockUtils.setGetCommitLogMock(gitLabService, 0, 0);

    given().pathParam("namespace", "top/dog/jello/tutti-frutti/iac").when().contentType(ContentType.JSON)
        .get("/api/v1/engagements/namespace/{namespace}").then().statusCode(200).body(is(
            "{\"archive_date\":\"20210125\",\"commits\":[],\"customer_contact_email\":\"reg@chiefs.com\",\"customer_contact_name\":\"Reg Dunlop\",\"customer_name\":\"customer1\",\"description\":\"Charleston\",\"end_date\":\"20201225\",\"engagement_lead_email\":\"doug93@leafs.com\",\"engagement_lead_name\":\"Doug Gilmour\",\"location\":\"Raleigh, NC\",\"project_id\":0,\"project_name\":\"project1\",\"public_reference\":false,\"start_date\":\"20200202\",\"technical_lead_email\":\"wendel17@leafs.com\",\"technical_lead_name\":\"Wendel Clark\"}"));
  }

  @Test
  void testCreateEngagementSuccess() {

    Group customerGroup = MockUtils.mockCustomerGroup("new");
    Group projectGroup = MockUtils.mockProjectGroup("new2");
    Project iacProject = MockUtils.mockIacProject();

    MockUtils.setGetProjectByIdMock(gitLabService, null, false, Optional.empty());
    MockUtils.setGetSubgroupsMock(gitLabService, Optional.empty(), false);
    MockUtils.setCreateGroupMock(gitLabService, customerGroup, projectGroup);
    MockUtils.setCreateProjectMock(gitLabService, iacProject);
    MockUtils.setCommitMultipleFilesMock(gitLabService, true);
    MockUtils.setGetFileMock(gitLabService, "runtime/webhooks.yaml", String.valueOf(iacProject.getId()), false);

    given().when().contentType(ContentType.JSON).queryParam("username", "jdoe")
        .queryParam("userEmail", "jdoe@email.com").body(ResourceLoader.load("engagement.json"))
        .post("/api/v1/engagements").then().statusCode(201);
  }

  @Test
  void testUpdateEngagementSuccess() {

    Group customerGroup = MockUtils.mockCustomerGroup("customer A");
    Group projectGroup = MockUtils.mockProjectGroup("project1");
    Project iacProject = MockUtils.mockIacProject();

    MockUtils.setGetProjectByIdMock(gitLabService, iacProject.getId(), true, Optional.of(iacProject));
    MockUtils.setGetGroupByIdOrPathMock(gitLabService, customerGroup.getId(), customerGroup);
    MockUtils.setGetGroupByIdOrPathMock(gitLabService, projectGroup.getId(), projectGroup);
    MockUtils.setGetSubgroupsMock(gitLabService, Optional.empty(), false);
    MockUtils.setCommitMultipleFilesMock(gitLabService, true);
    MockUtils.setGetFileMock(gitLabService, "runtime/webhooks.yaml", String.valueOf(iacProject.getId()), false);

    given().when().contentType(ContentType.JSON).queryParam("username", "jdoe")
        .queryParam("userEmail", "jdoe@email.com").body(ResourceLoader.load("engagement-update.json"))
        .post("/api/v1/engagements").then().statusCode(201);
  }

  @Test
  void testGetWebhooksSuccess() {

    Project p = Project.builder().id(99).build();
    MockUtils.setGetProjectByPathMock(gitLabService, "top/dog/jello/lemon/iac", true, Optional.of(p));
    MockUtils.setGetProjectHookMock(gitLabService, 99);

    given().when().contentType(ContentType.JSON).get("/api/v1/engagements/customer/jello/lemon/hooks").then()
        .statusCode(200).body(is("[{\"id\":13,\"project_id\":99,\"push_events\":true,\"push_events_branch_filter\""
            + ":\"master\",\"token\":\"token\",\"url\":\"http://webhook.edu/hook\"}]"));
  }

  @Test
  void testCreateProjectHookSuccess() {

    // Get Project
    Project p = Project.builder().id(66).build();
    MockUtils.setGetProjectByPathMock(gitLabService, "top/dog/jello/tutti-frutti/iac", true, Optional.of(p));
    MockUtils.setCreateProjectHookMock(gitLabService, 66);

    given().when().contentType(ContentType.JSON)
        .body("{\"push_events\": true, \"url\": \"https://lodestar/webhooks/blah\"}")
        .post("/api/v1/engagements/customer/jello/tutti-frutti/hooks").then().statusCode(201);
  }

  @Test
  void testCreateProjectHookFailAlreadyExists() {
    given().when().contentType(ContentType.JSON).body("{\"push_events\": true, \"url\": \"http://webhook.edu/hook\"}")
        .post("/api/v1/engagements/customer/jello/lemon/hooks").then().statusCode(400);
  }

  @Test
  void testCreateProjectHookFailNoProject() {
    given().when().contentType(ContentType.JSON)
        .body("{\"push_events\": true, \"url\": \"https://lodestar/webhooks/blah\"}")
        .post("/api/v1/engagements/customer/nope/tutti-frutti/hooks").then().statusCode(400);
  }

  @Test
  void testGetStatusSuccess() {

    MockUtils.setGetFileForStatusJsonMock(gitLabService, "top/dog/jello/lemon/iac", true);

    given().when().contentType(ContentType.JSON).get("/api/v1/engagements/customer/jello/lemon/status").then()
        .statusCode(200)
        .body(is(
            "{\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"},{\"message\":\"This is message 2\",\"severity\":\"CRITICAL\",\"updated\":\"2020-06-22T11:15:11Z\"},"
                + "{\"message\":\"This is message 3\",\"severity\":\"SUCCESS\",\"updated\":\"2020-06-22T10:25:31Z\"}],\"overall_status\":\"yellow\",\"subsystems\":[{\"access_urls\":[{\"Web Console\":\"https://console......\"},"
                + "{\"API\":\"https://api.....:6443\"}],\"info\":\"Deployment In Progress\",\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"openshift\","
                + "\"state\":\"provisioning\",\"status\":\"yellow\",\"updated\":\"2020-06-23T09:25:31Z\"},{\"access_urls\":[{\"atlassian\":\"https://mycompany.atlassian.net\"}],\"messages\":[{\"message\":\"This is message 1\","
                + "\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"atlassian\",\"state\":\"operational\",\"status\":\"green\",\"updated\":\"2020-06-22T20:15:37Z\"}]}"));
  }

  @Test
  void testGetProjectSuccess() {

    Project p = Project.builder().id(20).name("Project " + (20)).build();
    MockUtils.setGetProjectByPathMock(gitLabService, "top/dog/jello/lemon/iac", true, Optional.of(p));
    MockUtils.setGetFileForEngagementJsonMock(gitLabService, 20, true);
    MockUtils.setGetCommitLogMock(gitLabService, 0, 0);
    MockUtils.setGetFileForStatusJsonMock(gitLabService, 20, true);

    given().when().contentType(ContentType.JSON).queryParam("includeStatus", true)
        .get("/api/v1/engagements/customer/jello/lemon").then().statusCode(200).body(is(
            "{\"archive_date\":\"20210125\",\"commits\":[],\"customer_contact_email\":\"reg@chiefs.com\",\"customer_contact_name\":\"Reg Dunlop\",\"customer_name\":\"customer1\",\"description\":\"Charleston\",\"end_date\":\"20201225\",\"engagement_lead_email\":\"doug93@leafs.com\",\"engagement_lead_name\":\"Doug Gilmour\",\"location\":\"Raleigh, NC\",\"project_id\":0,\"project_name\":\"project1\",\"public_reference\":false,\"start_date\":\"20200202\",\"status\":{\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"},{\"message\":\"This is message 2\",\"severity\":\"CRITICAL\",\"updated\":\"2020-06-22T11:15:11Z\"},{\"message\":\"This is message 3\",\"severity\":\"SUCCESS\",\"updated\":\"2020-06-22T10:25:31Z\"}],\"overall_status\":\"yellow\",\"subsystems\":[{\"access_urls\":[{\"Web Console\":\"https://console......\"},{\"API\":\"https://api.....:6443\"}],\"info\":\"Deployment In Progress\",\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"openshift\",\"state\":\"provisioning\",\"status\":\"yellow\",\"updated\":\"2020-06-23T09:25:31Z\"},{\"access_urls\":[{\"atlassian\":\"https://mycompany.atlassian.net\"}],\"messages\":[{\"message\":\"This is message 1\",\"severity\":\"INFO\",\"updated\":\"2020-06-23T21:25:31Z\"}],\"name\":\"atlassian\",\"state\":\"operational\",\"status\":\"green\",\"updated\":\"2020-06-22T20:15:37Z\"}]},\"technical_lead_email\":\"wendel17@leafs.com\",\"technical_lead_name\":\"Wendel Clark\"}"));
  }

  @Test
  void testGetCommitsSuccess() {

    MockUtils.setGetCommitLogMock(gitLabService, "top/dog/jello/lemon/iac", 1);

    given().when().contentType(ContentType.JSON).get("/api/v1/engagements/customer/jello/lemon/commits").then()
        .statusCode(200)
        .body(is(
            "[{\"author_email\":\"mmarner@example.com\",\"author_name\":\"Mitch Marner\",\"authored_date\":\"2020-06-16T00:12:18.000+00:00\",\"committed_date\":\"2020-06-16T00:12:18.000+00:00\","
                + "\"id\":\"5178ffab3566ac591af95c3383d1c5916de4a3a9\",\"message\":\"Update engagement.json\",\"short_id\":\"5178ffab\",\"title\":\"Update engagement.json\","
                + "\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/5178ffab3566ac591af95c3383d1c5916de4a3a9\"},{\"author_email\":\"jtavares@example.com\",\"author_name\":\"John Tavares\","
                + "\"authored_date\":\"2020-06-11T16:46:19.000+00:00\",\"committed_date\":\"2020-06-11T16:46:19.000+00:00\",\"id\":\"7865570dc63b1463d9fb4d02bd09ff46d244e694\",\"message\":\"Update status.json\","
                + "\"short_id\":\"7865570d\",\"title\":\"Update status.json\",\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/7865570dc63b1463d9fb4d02bd09ff46d244e694\"},{\"author_email\":\"mmarner@example.com\","
                + "\"author_name\":\"Mitch Marner\",\"authored_date\":\"2020-06-04T22:34:10.000+00:00\",\"committed_date\":\"2020-06-04T22:34:10.000+00:00\",\"id\":\"dd0cc0fa7868210e2eb5a030f07cc0221dd6bc9f\","
                + "\"message\":\"Bump OCP version (jacob test)\",\"short_id\":\"dd0cc0fa\",\"title\":\"Bump OCP version (test)\",\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/dd0cc0fa7868210e2eb5a030f07cc0221dd6bc9f\"}]"));
  }

}
