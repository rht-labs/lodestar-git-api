package com.redhat.labs.omp.resources;

import com.redhat.labs.utils.ResourceLoader;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

class GroupsResourceTest {

    @Test
    public void testCreateGroup() throws InterruptedException {

        // wait a while after deletion, real GitLab is slow
        Thread.sleep(1000);

        // create the project
        createGroup(true);
    }

    private static void createGroup(boolean doAssert) {
        ValidatableResponse r = given()
                .when()
                .contentType(ContentType.JSON)
                .body(ResourceLoader.load("createGroup-001-request.json"))
                .post("/api/groups")
                .then();

        if (doAssert) {
            r.statusCode(200);
        }
    }
}