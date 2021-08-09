package com.redhat.labs.lodestar.resource;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MigrationResourceTest {
    @Test
    void testGetHookFileSuccess() {

        given().when().put("/api/migrate").then().statusCode(200);
        given().queryParam("participants", true).queryParam("uuid", true).queryParam("artifacts", true)
                .queryParam("hosting", true).when().put("/api/migrate").then().statusCode(200);
    }
}
