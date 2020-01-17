package com.redhat.labs.omp;

import com.redhat.labs.omp.models.GetMultipleFilesResponse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
public class TemplateResuorceIT {


    @Test
    public void testGetMetaDatFile() {
        GetMultipleFilesResponse getMultipleFilesResponse = given()
                .accept(ContentType.JSON)

                .when().get("/api/templates")
                .then()
                .statusCode(200)
                .extract()
                .as(GetMultipleFilesResponse.class);
                assertEquals(5, getMultipleFilesResponse.files.size());
//                .body("$.file.size()", is(1));

    }
}
