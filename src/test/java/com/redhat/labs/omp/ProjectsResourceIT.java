package com.redhat.labs.omp;

import com.redhat.labs.utils.ResourceLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void createYAMLFile() {
        // create a file
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(ResourceLoader.load("createYAMLFile-001-request.json"))
                .put("/api/projects")
                .then()
                .statusCode(200);
    }

    @Test
    public void createJSONFile() {
        // create a file
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(ResourceLoader.load("createJSONFile-001-request.json"))
                .put("/api/projects")
                .then()
                .statusCode(200);
    }

    @Test
    public void createTXTFile() {
        // create a file
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(ResourceLoader.load("createTXTFile-001-request.json"))
                .put("/api/projects")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteProject() throws InterruptedException {
        // delete the project
        deleteProject(true);
    }

    @Test
    public void testCreateProject() throws InterruptedException {
        // delete the project first, without caring about status code
        deleteProject(false);

        // wait a while after deletion, real GitLab is slow
        Thread.sleep(1000);

        // create the project
        createProject(true);
    }

    private static void deleteProject(boolean doAssert) {
        ValidatableResponse r = given()
                .when()
                .pathParam("project_id", "test/project")
                .delete("/api/projects/{project_id}")
                .then();

        if (doAssert) {
            r.statusCode(200);
        }
    }

    private static void createProject(boolean doAssert) {
        ValidatableResponse r = given()
                .when()
                .contentType(ContentType.JSON)
                .body(ResourceLoader.load("deleteProject-001-request.json"))
                .post("/api/projects")
                .then();

        if (doAssert) {
            r.statusCode(200);
        }
    }
}
