package com.redhat.labs.omp.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

import org.junit.jupiter.api.Test;

import com.redhat.labs.omp.models.Engagement;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class LegacyResourceTest {

    JsonbConfig config = new JsonbConfig()
            .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
    Jsonb jsonb = JsonbBuilder.create(config);

    // SUCCESS
    //  - customer doesn't exist ( get group, create group)
    //  - project doesn't exist (get group, create group)
    //  - project doesn't exist (get project, create project)
    //  - enable deployment key ( deployment key update)
    //  - create files
    //    - get inventory file
    //    - get each file in inventory file
    //    - commit multiple files

    @Test
    public void testCreateEngagementSuccess() {

        given()
            .when()
                .contentType(ContentType.JSON)
                .body(jsonb.toJson(mockEngagement()))
                .post("/api/residencies")
            .then()
                .statusCode(201)
                .header("Location", equalTo("http://localhost:8081/api/residencies/45"));

    }

    @Test
    public void testGetFile() {

        given()
            .when()
                .queryParam("name", "schema/meta.dat")
                .queryParam("repoId", 12)
                .get("/api/file")
            .then()
                .statusCode(200)
                .body("content", equalTo("./residency.yml"));

    }

    private Engagement mockEngagement() {

        return Engagement.builder()
                .customerName("customer1")
                .projectName("project1")
                .description("test")
                .location("Somewheresville")
                .startDate("20170501")
                .endDate("20170708")
                .archiveDate("20170930")
                .engagementLeadName("Mr. El")
                .engagementLeadEmail("mr.el@fake.com")
                .technicalLeadName("Mr Tl")
                .technicalLeadEmail("mr.tl@fake.com")
                .customerContactName("Mr Customer")
                .customerContactEmail("mr.customer@fake.com")
                .ocpCloudProviderName("GCP")
                .ocpCloudProviderRegion("West")
                .ocpVersion("v4.2")
                .ocpSubDomain("jello")
                .ocpPersistentStorageSize("50GB")
                .ocpClusterSize("medium")
                .build();

    }

}
