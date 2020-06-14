package com.redhat.labs.omp.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import com.redhat.labs.utils.ResourceLoader;

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
                .body(is("[{\"archive_date\":\"20210125\",\"customer_contact_email\":\"reg@chiefs.com\",\"customer_contact_name\":\"Reg Dunlop\","
                        + "\"customer_name\":\"customer1\",\"description\":\"Charleston\",\"end_date\":\"20201225\",\"engagement_lead_email\":\"doug93@leafs.com\","
                        + "\"engagement_lead_name\":\"Doug Gilmour\",\"location\":\"Raleigh, NC\",\"ocp_cloud_provider_name\":\"GCP\",\"ocp_cloud_provider_region\":\"West\","
                        + "\"ocp_cluster_size\":\"medium\",\"ocp_persistent_storage_size\":\"50GB\",\"ocp_sub_domain\":\"jello\",\"ocp_version\":\"v4.2\",\"project_id\":0,\"project_name\":\"project1\","
                        + "\"start_date\":\"20200202\",\"status\":{\"messages\":[\"This is message 1\",\"This is message 2\",\"This is message 3\"],\"openshift_api\":\"https://console.s11.core.rht-labs.com/\","
                        + "\"openshift_web_console\":\"https://console.s11.core.rht-labs.com/\",\"overall_status\":\"green\"},\"technical_lead_email\":\"wendel17@leafs.com\",\"technical_lead_name\":\"Wendel Clark\"}]"));

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
                .body(is("{\"messages\":[\"This is message 1\",\"This is message 2\",\"This is message 3\"],\"openshift_api\":\"https://console.s11.core.rht-labs.com/\",\"openshift_web_console\":\"https://console.s11.core.rht-labs.com/\","
                        + "\"overall_status\":\"green\"}"));
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
                .body(is("{\"archive_date\":\"20210125\",\"customer_contact_email\":\"reg@chiefs.com\",\"customer_contact_name\":\"Reg Dunlop\",\"customer_name\":\"customer1\",\"description\":\"Charleston\",\"end_date\":\"20201225\","
                        + "\"engagement_lead_email\":\"doug93@leafs.com\",\"engagement_lead_name\":\"Doug Gilmour\",\"location\":\"Raleigh, NC\",\"ocp_cloud_provider_name\":\"GCP\",\"ocp_cloud_provider_region\":\"West\",\"ocp_cluster_size\":\"medium\","
                        + "\"ocp_persistent_storage_size\":\"50GB\",\"ocp_sub_domain\":\"jello\",\"ocp_version\":\"v4.2\",\"project_id\":0,\"project_name\":\"project1\",\"start_date\":\"20200202\","
                        + "\"status\":{\"messages\":[\"This is message 1\",\"This is message 2\",\"This is message 3\"],\"openshift_api\":\"https://console.s11.core.rht-labs.com/\",\"openshift_web_console\":\"https://console.s11.core.rht-labs.com/\","
                        + "\"overall_status\":\"green\"},\"technical_lead_email\":\"wendel17@leafs.com\",\"technical_lead_name\":\"Wendel Clark\"}"));
    }
     
    
}
