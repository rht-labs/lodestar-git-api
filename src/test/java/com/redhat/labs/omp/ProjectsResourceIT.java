package com.redhat.labs.omp;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.redhat.labs.utils.ResourceLoader;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class ProjectsResourceIT {
    @Test
    public void testListAllProjects() {
        String projectList = given()
                .when().get("/api/projects")
                .then()
                .statusCode(200).extract().asString();

        assertTrue(projectList.contains("https://gitlab.consulting.redhat.com/api/v4/projects/10816/events"));
    }

    @Test
    public void testDeleteProject() throws InterruptedException {
        given()
                .when()
                .pathParam("project_id", "test/project")
                .delete("/api/projects/{project_id}")
                .then().statusCode(200);
    }

    @Test
    public void testCreateProject() throws InterruptedException {
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(ResourceLoader.load("deleteProject-001-request.json"))
                .post("/api/projects")
                .then().statusCode(200);
    }
}
