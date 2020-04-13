package com.redhat.labs.omp.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class VersionResourceTest {

    @Test
    public void testValidResourceVersion() {
        given()
        .when()
            .get("/api/v1/version")
        .then()
            .statusCode(200)
            .body(is("{\"git_commit\":\"not.set\",\"git_tag\":\"not.set\"}"));
    }   
}
    