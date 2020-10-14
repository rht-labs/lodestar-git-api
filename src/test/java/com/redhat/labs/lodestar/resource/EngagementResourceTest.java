package com.redhat.labs.lodestar.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import com.redhat.labs.lodestar.utils.ResourceLoader;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class EngagementResourceTest {

    
    @Test
    void testGetAllEngagementsSuccess() {
        
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
    void testGetEngagementByNamespace() {
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
        given()
            .when()
                .contentType(ContentType.JSON)
                .get("/api/v1/engagements/customer/jello/lemon/commits")
            .then()
                .statusCode(200)
                .body(is("[{\"author_email\":\"mmarner@example.com\",\"author_name\":\"Mitch Marner\",\"authored_date\":\"2020-06-16T00:12:18.000+00:00\",\"committed_date\":\"2020-06-16T00:12:18.000+00:00\","
                        + "\"id\":\"5178ffab3566ac591af95c3383d1c5916de4a3a9\",\"message\":\"Update engagement.json\",\"short_id\":\"5178ffab\",\"title\":\"Update engagement.json\","
                        + "\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/5178ffab3566ac591af95c3383d1c5916de4a3a9\"},{\"author_email\":\"jtavares@example.com\",\"author_name\":\"John Tavares\","
                        + "\"authored_date\":\"2020-06-11T16:46:19.000+00:00\",\"committed_date\":\"2020-06-11T16:46:19.000+00:00\",\"id\":\"7865570dc63b1463d9fb4d02bd09ff46d244e694\",\"message\":\"Update status.json\","
                        + "\"short_id\":\"7865570d\",\"title\":\"Update status.json\",\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/7865570dc63b1463d9fb4d02bd09ff46d244e694\"},{\"author_email\":\"mmarner@example.com\","
                        + "\"author_name\":\"Mitch Marner\",\"authored_date\":\"2020-06-04T22:34:10.000+00:00\",\"committed_date\":\"2020-06-04T22:34:10.000+00:00\",\"id\":\"dd0cc0fa7868210e2eb5a030f07cc0221dd6bc9f\","
                        + "\"message\":\"Bump OCP version (jacob test)\",\"short_id\":\"dd0cc0fa\",\"title\":\"Bump OCP version (test)\",\"web_url\":\"https://gitlab.example.com/store/jello/lemon/iac/-/commit/dd0cc0fa7868210e2eb5a030f07cc0221dd6bc9f\"}]"));
    }
     
    
}
