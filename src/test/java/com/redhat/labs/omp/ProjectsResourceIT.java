package com.redhat.labs.omp;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import com.redhat.labs.utils.ResourceLoader;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class ProjectsResourceIT {

    @Test
    public void testDeleteProject() throws InterruptedException {
        given()
                .when()
                .pathParam("project_id", 1234)
                .delete("/api/v1/projects/{project_id}")
                .then().statusCode(204);
    }

    @Test
    public void testCreateProject() throws InterruptedException {
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(ResourceLoader.load("deleteProject-001-request.json"))
                .post("/api/v1/projects")
                .then().statusCode(201);
    }
}
