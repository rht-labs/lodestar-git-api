package com.redhat.labs.lodestar.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class ConfigResourceTest {
   
    @Test
    void testGetHookFileSuccess() {

        given()
        .when()
            .contentType(ContentType.JSON)
            .get("/api/v2/config/webhooks")
        .then()
            .statusCode(200)
            .body(is("\n[\n" + 
                    "    {\n" + 
                    "        \"baseUrl\": \"https://labs.com/webhooks/\",\n" + 
                    "        \"enabledAfterArchive\": false,\n" +
                    "        \"name\": \"labs\",\n" + 
                    "        \"pushEvent\": true,\n" + 
                    "        \"pushEventsBranchFilter\": \"master\",\n" + 
                    "        \"token\": \"abc\"\n" + 
                    "    },\n" + 
                    "    {\n" + 
                    "        \"baseUrl\": \"https://rht.com/hooks/\",\n" + 
                    "        \"enabledAfterArchive\": true,\n" +
                    "        \"name\": \"rht\",\n" + 
                    "        \"pushEvent\": true,\n" + 
                    "        \"pushEventsBranchFilter\": \"master\",\n" + 
                    "        \"token\": \"def\"\n" + 
                    "    }\n" + 
                    "]"));

    }

}
