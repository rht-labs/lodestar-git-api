package com.redhat.labs.omp.resources;

import com.redhat.labs.omp.models.CreateResidencyGroupStructure;
import com.redhat.labs.omp.models.GitLabCreateProjectResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class GroupsResourceTest {
    @Test
    public void testCreateGroupStructure() throws InterruptedException {
        CreateResidencyGroupStructure createResidencyGroupStructure = new CreateResidencyGroupStructure();
        createResidencyGroupStructure.projectName = System.currentTimeMillis() + "quarkus-project-name";
        createResidencyGroupStructure.customerName = "quarkus-customer-name";

        GitLabCreateProjectResponse gitLabCreateProjectResponse = given()
                .when()
                .contentType(ContentType.JSON)
                .body(createResidencyGroupStructure)
                .post("/api/groups")
                .then()
                .extract()
                .as(GitLabCreateProjectResponse.class);


        assertNotNull(gitLabCreateProjectResponse.id);
    }
}