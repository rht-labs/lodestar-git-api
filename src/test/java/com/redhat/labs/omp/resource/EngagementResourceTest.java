package com.redhat.labs.omp.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import com.redhat.labs.utils.ResourceLoader;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class EngagementResourceTest {

    
    @Test
    public void testGetAllEngagementsSuccess() {
        
        given()
            .when()
                .contentType(ContentType.JSON)
                .get("/api/v1/engagements")
            .then()
                .statusCode(200)
                .body(is("[{\"archive_date\":\"20210125\",\"customer_contact_email\":\"reg@chiefs.com\",\"customer_contact_name\":\"Reg Dunlop\",\"customer_name\":\"customer1\","
                        + "\"description\":\"Charleston\",\"end_date\":\"20201225\",\"engagement_lead_email\":\"doug93@leafs.com\",\"engagement_lead_name\":\"Doug Gilmour\","
                        + "\"id\":0,\"location\":\"Raleigh, NC\",\"ocp_cloud_provider_name\":\"GCP\",\"ocp_cloud_provider_region\":\"West\",\"ocp_cluster_size\":\"medium\","
                        + "\"ocp_persistent_storage_size\":\"50GB\",\"ocp_sub_domain\":\"jello\",\"ocp_version\":\"v4.2\",\"project_name\":\"project1\",\"start_date\":\"20200202\","
                        + "\"technical_lead_email\":\"wendel17@leafs.com\",\"technical_lead_name\":\"Wendel Clark\"}]"));

    }
    
    @Test
    public void testCreateEngagementSuccess() {
        given()
            .when()
                .contentType(ContentType.JSON)
                .body(ResourceLoader.load("engagement.json"))
                .post("/api/v1/engagements")
            .then()
                .statusCode(201);
    }
    
}
