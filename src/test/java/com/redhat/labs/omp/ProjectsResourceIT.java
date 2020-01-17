package com.redhat.labs.omp;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class ProjectsResourceIT {
    public static final String NAMESPACE = "lab20";
    public static final String PROJECT_NAME = "quarkus-integration-test";

    @BeforeAll
    public static void init() {
        try {
            createProject(false);
        } catch (Throwable e) {
            // ignored by design
        }
    }

    @AfterAll
    public static void teardown() {
        try {
            deleteProject(false);
        } catch (Throwable e) {
            // ignored by design
        }
    }

    @Test
    public void testListAllProjects() {
        given()
                .when().get("/api/projects")
                .then()
                .statusCode(200);
    }

    @Test
    public void createYAMLFile() {
        // create a file
        given()
                .when()
                .contentType(ContentType.JSON)
                .body("{ \"project_id\" : \"" + NAMESPACE + "/" + PROJECT_NAME + "\", \"file_path\" : \"employees.yaml\", \"content\" : {\n" +
                        "\"employees\":[\n" +
                        "    {\"firstName\":\"John\", \"lastName\":\"Doe\"},\n" +
                        "    {\"firstName\":\"Anna\", \"lastName\":\"Smith\"},\n" +
                        "    {\"firstName\":\"Peter\", \"lastName\":\"Jones\"}\n" +
                        "]\n" +
                        "}, \"comment\" : \"created by OMP Git API integration tests\", \"output_format\": \"YAML\" }")
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
                .body("{ \"project_id\" : \"" + NAMESPACE + "/" + PROJECT_NAME + "\", \"file_path\" : \"employees.json\", \"content\" : {\n" +
                        "\"employees\":[\n" +
                        "    {\"firstName\":\"John\", \"lastName\":\"Doe\"},\n" +
                        "    {\"firstName\":\"Anna\", \"lastName\":\"Smith\"},\n" +
                        "    {\"firstName\":\"Peter\", \"lastName\":\"Jones\"}\n" +
                        "]\n" +
                        "}, \"comment\" : \"created by OMP Git API integration tests\", \"output_format\": \"JSON\" }")
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
                .body("{ \"project_id\" : \"" + NAMESPACE + "/" + PROJECT_NAME + "\"," +
                        " \"file_path\" : \"hello-world.txt\", " +
                        "\"content\" : \"hello world!\", " +
                        "\"comment\" : \"created by OMP Git API integration tests\" }")
                .put("/api/projects")
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteProject() throws InterruptedException {
        // delete the project
        deleteProject(true);

        // wait a while after deletion, real GitLab is slow
        Thread.sleep(1000);

        // create the project
        createProject(false);
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
                .pathParam("project_id", NAMESPACE + "/" + PROJECT_NAME)
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
                .body("{ \"residency_name\" : \"" + PROJECT_NAME + "\" }")
                .post("/api/projects")
                .then();

        if (doAssert) {
            r.statusCode(200);
        }
    }
}
