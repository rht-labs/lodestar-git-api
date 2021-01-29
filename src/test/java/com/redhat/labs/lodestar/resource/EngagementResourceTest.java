package com.redhat.labs.lodestar.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        MockUtils.setGetProjectsByGroupMock(gitLabService, 20, projects, true);

        // get engagement file
        MockUtils.setGetFileForEngagementJsonMock(gitLabService, 20, true);

        // get commits
        MockUtils.setGetCommitLogMock(gitLabService, 0, 0);

        // get status file
        MockUtils.setGetFileForStatusJsonMock(gitLabService, 20, true);

        given().when().contentType(ContentType.JSON).get("/api/v1/engagements").then().statusCode(200).body(is(
                "\n[\n" + 
                "    {\n" + 
                "        \"archive_date\": \"20210125\",\n" + 
                "        \"commits\": [\n" + 
                "        ],\n" + 
                "        \"customer_contact_email\": \"reg@chiefs.com\",\n" + 
                "        \"customer_contact_name\": \"Reg Dunlop\",\n" + 
                "        \"customer_name\": \"customer1\",\n" + 
                "        \"description\": \"Charleston\",\n" + 
                "        \"end_date\": \"20201225\",\n" + 
                "        \"engagement_lead_email\": \"doug93@leafs.com\",\n" + 
                "        \"engagement_lead_name\": \"Doug Gilmour\",\n" + 
                "        \"location\": \"Raleigh, NC\",\n" + 
                "        \"project_id\": 0,\n" + 
                "        \"project_name\": \"project1\",\n" + 
                "        \"public_reference\": false,\n" + 
                "        \"start_date\": \"20200202\",\n" + 
                "        \"status\": {\n" + 
                "            \"messages\": [\n" + 
                "                {\n" + 
                "                    \"message\": \"This is message 1\",\n" + 
                "                    \"severity\": \"INFO\",\n" + 
                "                    \"updated\": \"2020-06-23T21:25:31Z\"\n" + 
                "                },\n" + 
                "                {\n" + 
                "                    \"message\": \"This is message 2\",\n" + 
                "                    \"severity\": \"CRITICAL\",\n" + 
                "                    \"updated\": \"2020-06-22T11:15:11Z\"\n" + 
                "                },\n" + 
                "                {\n" + 
                "                    \"message\": \"This is message 3\",\n" + 
                "                    \"severity\": \"SUCCESS\",\n" + 
                "                    \"updated\": \"2020-06-22T10:25:31Z\"\n" + 
                "                }\n" + 
                "            ],\n" + 
                "            \"overall_status\": \"yellow\",\n" + 
                "            \"subsystems\": [\n" + 
                "                {\n" + 
                "                    \"access_urls\": [\n" + 
                "                        {\n" + 
                "                            \"Web Console\": \"https://console......\"\n" + 
                "                        },\n" + 
                "                        {\n" + 
                "                            \"API\": \"https://api.....:6443\"\n" + 
                "                        }\n" + 
                "                    ],\n" + 
                "                    \"info\": \"Deployment In Progress\",\n" + 
                "                    \"messages\": [\n" + 
                "                        {\n" + 
                "                            \"message\": \"This is message 1\",\n" + 
                "                            \"severity\": \"INFO\",\n" + 
                "                            \"updated\": \"2020-06-23T21:25:31Z\"\n" + 
                "                        }\n" + 
                "                    ],\n" + 
                "                    \"name\": \"openshift\",\n" + 
                "                    \"state\": \"provisioning\",\n" + 
                "                    \"status\": \"yellow\",\n" + 
                "                    \"updated\": \"2020-06-23T09:25:31Z\"\n" + 
                "                },\n" + 
                "                {\n" + 
                "                    \"access_urls\": [\n" + 
                "                        {\n" + 
                "                            \"atlassian\": \"https://mycompany.atlassian.net\"\n" + 
                "                        }\n" + 
                "                    ],\n" + 
                "                    \"messages\": [\n" + 
                "                        {\n" + 
                "                            \"message\": \"This is message 1\",\n" + 
                "                            \"severity\": \"INFO\",\n" + 
                "                            \"updated\": \"2020-06-23T21:25:31Z\"\n" + 
                "                        }\n" + 
                "                    ],\n" + 
                "                    \"name\": \"atlassian\",\n" + 
                "                    \"state\": \"operational\",\n" + 
                "                    \"status\": \"green\",\n" + 
                "                    \"updated\": \"2020-06-22T20:15:37Z\"\n" + 
                "                }\n" + 
                "            ]\n" + 
                "        },\n" + 
                "        \"technical_lead_email\": \"wendel17@leafs.com\",\n" + 
                "        \"technical_lead_name\": \"Wendel Clark\"\n" + 
                "    }\n" + 
                "]"));

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
                        "\n{\n" + 
                        "    \"archive_date\": \"20210125\",\n" + 
                        "    \"commits\": [\n" + 
                        "    ],\n" + 
                        "    \"customer_contact_email\": \"reg@chiefs.com\",\n" + 
                        "    \"customer_contact_name\": \"Reg Dunlop\",\n" + 
                        "    \"customer_name\": \"customer1\",\n" + 
                        "    \"description\": \"Charleston\",\n" + 
                        "    \"end_date\": \"20201225\",\n" + 
                        "    \"engagement_lead_email\": \"doug93@leafs.com\",\n" + 
                        "    \"engagement_lead_name\": \"Doug Gilmour\",\n" + 
                        "    \"location\": \"Raleigh, NC\",\n" + 
                        "    \"project_id\": 0,\n" + 
                        "    \"project_name\": \"project1\",\n" + 
                        "    \"public_reference\": false,\n" + 
                        "    \"start_date\": \"20200202\",\n" + 
                        "    \"technical_lead_email\": \"wendel17@leafs.com\",\n" + 
                        "    \"technical_lead_name\": \"Wendel Clark\"\n" + 
                        "}"));
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
                .statusCode(200)
                .body(is("\n[\n" + 
                        "    {\n" + 
                        "        \"id\": 13,\n" + 
                        "        \"project_id\": 99,\n" + 
                        "        \"push_events\": true,\n" + 
                        "        \"push_events_branch_filter\": \"master\",\n" + 
                        "        \"token\": \"token\",\n" + 
                        "        \"url\": \"http://webhook.edu/hook\"\n" + 
                        "    }\n" + 
                        "]"));
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
        given().when().contentType(ContentType.JSON)
                .body("{\"push_events\": true, \"url\": \"http://webhook.edu/hook\"}")
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
                        "\n{\n" + 
                        "    \"messages\": [\n" + 
                        "        {\n" + 
                        "            \"message\": \"This is message 1\",\n" + 
                        "            \"severity\": \"INFO\",\n" + 
                        "            \"updated\": \"2020-06-23T21:25:31Z\"\n" + 
                        "        },\n" + 
                        "        {\n" + 
                        "            \"message\": \"This is message 2\",\n" + 
                        "            \"severity\": \"CRITICAL\",\n" + 
                        "            \"updated\": \"2020-06-22T11:15:11Z\"\n" + 
                        "        },\n" + 
                        "        {\n" + 
                        "            \"message\": \"This is message 3\",\n" + 
                        "            \"severity\": \"SUCCESS\",\n" + 
                        "            \"updated\": \"2020-06-22T10:25:31Z\"\n" + 
                        "        }\n" + 
                        "    ],\n" + 
                        "    \"overall_status\": \"yellow\",\n" + 
                        "    \"subsystems\": [\n" + 
                        "        {\n" + 
                        "            \"access_urls\": [\n" + 
                        "                {\n" + 
                        "                    \"Web Console\": \"https://console......\"\n" + 
                        "                },\n" + 
                        "                {\n" + 
                        "                    \"API\": \"https://api.....:6443\"\n" + 
                        "                }\n" + 
                        "            ],\n" + 
                        "            \"info\": \"Deployment In Progress\",\n" + 
                        "            \"messages\": [\n" + 
                        "                {\n" + 
                        "                    \"message\": \"This is message 1\",\n" + 
                        "                    \"severity\": \"INFO\",\n" + 
                        "                    \"updated\": \"2020-06-23T21:25:31Z\"\n" + 
                        "                }\n" + 
                        "            ],\n" + 
                        "            \"name\": \"openshift\",\n" + 
                        "            \"state\": \"provisioning\",\n" + 
                        "            \"status\": \"yellow\",\n" + 
                        "            \"updated\": \"2020-06-23T09:25:31Z\"\n" + 
                        "        },\n" + 
                        "        {\n" + 
                        "            \"access_urls\": [\n" + 
                        "                {\n" + 
                        "                    \"atlassian\": \"https://mycompany.atlassian.net\"\n" + 
                        "                }\n" + 
                        "            ],\n" + 
                        "            \"messages\": [\n" + 
                        "                {\n" + 
                        "                    \"message\": \"This is message 1\",\n" + 
                        "                    \"severity\": \"INFO\",\n" + 
                        "                    \"updated\": \"2020-06-23T21:25:31Z\"\n" + 
                        "                }\n" + 
                        "            ],\n" + 
                        "            \"name\": \"atlassian\",\n" + 
                        "            \"state\": \"operational\",\n" + 
                        "            \"status\": \"green\",\n" + 
                        "            \"updated\": \"2020-06-22T20:15:37Z\"\n" + 
                        "        }\n" + 
                        "    ]\n" + 
                        "}"));
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
                        "\n{\n" + 
                        "    \"archive_date\": \"20210125\",\n" + 
                        "    \"commits\": [\n" + 
                        "    ],\n" + 
                        "    \"customer_contact_email\": \"reg@chiefs.com\",\n" + 
                        "    \"customer_contact_name\": \"Reg Dunlop\",\n" + 
                        "    \"customer_name\": \"customer1\",\n" + 
                        "    \"description\": \"Charleston\",\n" + 
                        "    \"end_date\": \"20201225\",\n" + 
                        "    \"engagement_lead_email\": \"doug93@leafs.com\",\n" + 
                        "    \"engagement_lead_name\": \"Doug Gilmour\",\n" + 
                        "    \"location\": \"Raleigh, NC\",\n" + 
                        "    \"project_id\": 0,\n" + 
                        "    \"project_name\": \"project1\",\n" + 
                        "    \"public_reference\": false,\n" + 
                        "    \"start_date\": \"20200202\",\n" + 
                        "    \"status\": {\n" + 
                        "        \"messages\": [\n" + 
                        "            {\n" + 
                        "                \"message\": \"This is message 1\",\n" + 
                        "                \"severity\": \"INFO\",\n" + 
                        "                \"updated\": \"2020-06-23T21:25:31Z\"\n" + 
                        "            },\n" + 
                        "            {\n" + 
                        "                \"message\": \"This is message 2\",\n" + 
                        "                \"severity\": \"CRITICAL\",\n" + 
                        "                \"updated\": \"2020-06-22T11:15:11Z\"\n" + 
                        "            },\n" + 
                        "            {\n" + 
                        "                \"message\": \"This is message 3\",\n" + 
                        "                \"severity\": \"SUCCESS\",\n" + 
                        "                \"updated\": \"2020-06-22T10:25:31Z\"\n" + 
                        "            }\n" + 
                        "        ],\n" + 
                        "        \"overall_status\": \"yellow\",\n" + 
                        "        \"subsystems\": [\n" + 
                        "            {\n" + 
                        "                \"access_urls\": [\n" + 
                        "                    {\n" + 
                        "                        \"Web Console\": \"https://console......\"\n" + 
                        "                    },\n" + 
                        "                    {\n" + 
                        "                        \"API\": \"https://api.....:6443\"\n" + 
                        "                    }\n" + 
                        "                ],\n" + 
                        "                \"info\": \"Deployment In Progress\",\n" + 
                        "                \"messages\": [\n" + 
                        "                    {\n" + 
                        "                        \"message\": \"This is message 1\",\n" + 
                        "                        \"severity\": \"INFO\",\n" + 
                        "                        \"updated\": \"2020-06-23T21:25:31Z\"\n" + 
                        "                    }\n" + 
                        "                ],\n" + 
                        "                \"name\": \"openshift\",\n" + 
                        "                \"state\": \"provisioning\",\n" + 
                        "                \"status\": \"yellow\",\n" + 
                        "                \"updated\": \"2020-06-23T09:25:31Z\"\n" + 
                        "            },\n" + 
                        "            {\n" + 
                        "                \"access_urls\": [\n" + 
                        "                    {\n" + 
                        "                        \"atlassian\": \"https://mycompany.atlassian.net\"\n" + 
                        "                    }\n" + 
                        "                ],\n" + 
                        "                \"messages\": [\n" + 
                        "                    {\n" + 
                        "                        \"message\": \"This is message 1\",\n" + 
                        "                        \"severity\": \"INFO\",\n" + 
                        "                        \"updated\": \"2020-06-23T21:25:31Z\"\n" + 
                        "                    }\n" + 
                        "                ],\n" + 
                        "                \"name\": \"atlassian\",\n" + 
                        "                \"state\": \"operational\",\n" + 
                        "                \"status\": \"green\",\n" + 
                        "                \"updated\": \"2020-06-22T20:15:37Z\"\n" + 
                        "            }\n" + 
                        "        ]\n" + 
                        "    },\n" + 
                        "    \"technical_lead_email\": \"wendel17@leafs.com\",\n" + 
                        "    \"technical_lead_name\": \"Wendel Clark\"\n" + 
                        "}"));
    }

    @Test
    void testGetCommitsSuccess() {

        MockUtils.setGetCommitLogMock(gitLabService, "top/dog/jello/lemon/iac", 1);

        given().when().contentType(ContentType.JSON).get("/api/v1/engagements/customer/jello/lemon/commits").then()
                .statusCode(200)
                .body(is(
                        "\n[\n" + 
                        "    {\n" + 
                        "        \"author_email\": \"mmarner@example.com\",\n" + 
                        "        \"author_name\": \"Mitch Marner\",\n" + 
                        "        \"authored_date\": \"2020-06-16T00:12:18.000+00:00\",\n" + 
                        "        \"committed_date\": \"2020-06-16T00:12:18.000+00:00\",\n" + 
                        "        \"id\": \"5178ffab3566ac591af95c3383d1c5916de4a3a9\",\n" + 
                        "        \"message\": \"Update engagement.json\",\n" + 
                        "        \"short_id\": \"5178ffab\",\n" + 
                        "        \"title\": \"Update engagement.json\",\n" + 
                        "        \"web_url\": \"https://gitlab.example.com/store/jello/lemon/iac/-/commit/5178ffab3566ac591af95c3383d1c5916de4a3a9\"\n" + 
                        "    },\n" + 
                        "    {\n" + 
                        "        \"author_email\": \"jtavares@example.com\",\n" + 
                        "        \"author_name\": \"John Tavares\",\n" + 
                        "        \"authored_date\": \"2020-06-11T16:46:19.000+00:00\",\n" + 
                        "        \"committed_date\": \"2020-06-11T16:46:19.000+00:00\",\n" + 
                        "        \"id\": \"7865570dc63b1463d9fb4d02bd09ff46d244e694\",\n" + 
                        "        \"message\": \"Update status.json\",\n" + 
                        "        \"short_id\": \"7865570d\",\n" + 
                        "        \"title\": \"Update status.json\",\n" + 
                        "        \"web_url\": \"https://gitlab.example.com/store/jello/lemon/iac/-/commit/7865570dc63b1463d9fb4d02bd09ff46d244e694\"\n" + 
                        "    },\n" + 
                        "    {\n" + 
                        "        \"author_email\": \"mmarner@example.com\",\n" + 
                        "        \"author_name\": \"Mitch Marner\",\n" + 
                        "        \"authored_date\": \"2020-06-04T22:34:10.000+00:00\",\n" + 
                        "        \"committed_date\": \"2020-06-04T22:34:10.000+00:00\",\n" + 
                        "        \"id\": \"dd0cc0fa7868210e2eb5a030f07cc0221dd6bc9f\",\n" + 
                        "        \"message\": \"Bump OCP version (jacob test)\",\n" + 
                        "        \"short_id\": \"dd0cc0fa\",\n" + 
                        "        \"title\": \"Bump OCP version (test)\",\n" + 
                        "        \"web_url\": \"https://gitlab.example.com/store/jello/lemon/iac/-/commit/dd0cc0fa7868210e2eb5a030f07cc0221dd6bc9f\"\n" + 
                        "    }\n" + 
                        "]"));
    }

    @Test
    void testDeleteEngagement() throws InterruptedException {

        Project project = MockUtils.mockIacProject();
        MockUtils.setGetProjectByPathMock(gitLabService, "top/dog/customer1/project1/iac", true, Optional.of(project));
        MockUtils.setGetFileForEngagementJsonMock(gitLabService, project.getId(), true);
        MockUtils.setGetCommitLogMock(gitLabService, 0, 0);
        MockUtils.setGetSubgroupsMock(gitLabService, Optional.empty(), false);
        MockUtils.setGetGroupByIdOrPathMock(gitLabService, "customer1", "project1");
        
        MockUtils.setGetProjectsByGroupMock(gitLabService, project.getId(), Arrays.asList(), false);
        MockUtils.setDeleteGroupById(gitLabService);

        given()
        .when()
            .delete("/api/v1/engagements/customer/customer1/project1")
        .then()
            .statusCode(202);

        // validate delete project was called
        Mockito.verify(gitLabService).deleteProjectById(project.getId());
        
    }

}
