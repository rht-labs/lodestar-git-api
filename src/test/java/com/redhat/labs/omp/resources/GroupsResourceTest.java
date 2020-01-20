package com.redhat.labs.omp.resources;

import com.redhat.labs.omp.models.CreateGroupResponse;
import com.redhat.labs.utils.ResourceLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GroupsResourceTest {

    @Test
    public void testCreateGroup() throws InterruptedException {

        CreateGroupResponse createGroupResponse = given()
                .when()
                .contentType(ContentType.JSON)
                .body(ResourceLoader.load("createGroup-001-request.json"))
                .post("/api/groups")
                .then()
                .extract()
                .as(CreateGroupResponse.class);


           assertNotNull(createGroupResponse.id);

    }


}