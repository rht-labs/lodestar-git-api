package com.redhat.labs.omp.resources;

import com.redhat.labs.omp.models.CreateResidencyGroupStructure;
import com.redhat.labs.omp.models.gitlab.response.GitLabCreateProjectResponse;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

@QuarkusTest
class GroupsResourceTest {
    @Test
    public void testCreateGroupStructure() throws InterruptedException {
        CreateResidencyGroupStructure group = new CreateResidencyGroupStructure();
        group.projectName = System.currentTimeMillis() + "quarkus-project-name";
        group.customerName = "quarkus-customer-name";
        JsonbConfig config = new JsonbConfig().withPropertyNamingStrategy(
            PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);

        String json = JsonbBuilder.create(config).toJson(group);

        GitLabCreateProjectResponse gitLabCreateProjectResponse = given()
                .when()
                .contentType(ContentType.JSON)
                .body(json)
                .post("/api/groups")
                .then()
                .extract()
                .as(GitLabCreateProjectResponse.class);


        assertNotNull(gitLabCreateProjectResponse.id);
    }
}