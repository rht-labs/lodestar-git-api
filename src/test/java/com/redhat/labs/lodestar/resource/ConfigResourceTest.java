package com.redhat.labs.lodestar.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class ConfigResourceTest {

    @Test
    void testGetConfigFileSuccess() {

        given().when().contentType(ContentType.JSON).get("/api/v1/config").then().statusCode(200).body(is(
                "\n{\n" + 
                "    \"content\": \"---\\nproviders:\\n- label: AWS\\n  value: ec2\\n  regions:\\n  - label: US East 1 (N. Virginia)\\n    value: us-east-1\\n  - label: US East 2 (Ohio)\\n    value: us-east-2\\nopenshift:\\n  versions:\\n  - label: v4.1\\n    value: 4.1.31\\n  - label: v4.2\\n    value: 4.2.16\\n  - label: v4.3\\n    value: 4.3.0\\n  persistent-storage:\\n  - label: None\\n    value: none\\n  - label: 50GB\\n    value: 50G\\n  - label: 100GB\\n    value: 100G\\n  - label: 250GB\\n    value: 250G\\n  - label: 500GB\\n    value: 500G\\n  cluster-size:\\n  - label: Small\\n    value: small\\nuser-management:\\n  rbac:\\n    roles:\\n    - label: Developer \\n      value: developer\\n    - label: Observer \\n      value: observer\\n    - label: Admin \\n      value: admin\\n\",\n" + 
                "    \"encoding\": \"base64\",\n" + 
                "    \"file_path\": \"runtime/lodestar-runtime-config.yaml\"\n" + 
                "}"));

    }

    @Test
    void testGetConfigFileSuccessV2() {

        given().when().contentType(ContentType.JSON).get("/api/v2/config").then().statusCode(200).body(is(
                "\n{\n" + 
                "    \"providers\": [\n" + 
                "        {\n" + 
                "            \"label\": \"AWS\",\n" + 
                "            \"value\": \"ec2\",\n" + 
                "            \"regions\": [\n" + 
                "                {\n" + 
                "                    \"label\": \"US East 1 (N. Virginia)\",\n" + 
                "                    \"value\": \"us-east-1\"\n" + 
                "                },\n" + 
                "                {\n" + 
                "                    \"label\": \"US East 2 (Ohio)\",\n" + 
                "                    \"value\": \"us-east-2\"\n" + 
                "                }\n" + 
                "            ]\n" + 
                "        }\n" + 
                "    ],\n" + 
                "    \"openshift\": {\n" + 
                "        \"versions\": [\n" + 
                "            {\n" + 
                "                \"label\": \"v4.1\",\n" + 
                "                \"value\": \"4.1.31\"\n" + 
                "            },\n" + 
                "            {\n" + 
                "                \"label\": \"v4.2\",\n" + 
                "                \"value\": \"4.2.16\"\n" + 
                "            },\n" + 
                "            {\n" + 
                "                \"label\": \"v4.3\",\n" + 
                "                \"value\": \"4.3.0\"\n" + 
                "            }\n" + 
                "        ],\n" + 
                "        \"persistent-storage\": [\n" + 
                "            {\n" + 
                "                \"label\": \"None\",\n" + 
                "                \"value\": \"none\"\n" + 
                "            },\n" + 
                "            {\n" + 
                "                \"label\": \"50GB\",\n" + 
                "                \"value\": \"50G\"\n" + 
                "            },\n" + 
                "            {\n" + 
                "                \"label\": \"100GB\",\n" + 
                "                \"value\": \"100G\"\n" + 
                "            },\n" + 
                "            {\n" + 
                "                \"label\": \"250GB\",\n" + 
                "                \"value\": \"250G\"\n" + 
                "            },\n" + 
                "            {\n" + 
                "                \"label\": \"500GB\",\n" + 
                "                \"value\": \"500G\"\n" + 
                "            }\n" + 
                "        ],\n" + 
                "        \"cluster-size\": [\n" + 
                "            {\n" + 
                "                \"label\": \"Small\",\n" + 
                "                \"value\": \"small\"\n" + 
                "            }\n" + 
                "        ]\n" + 
                "    },\n" + 
                "    \"user-management\": {\n" + 
                "        \"rbac\": {\n" + 
                "            \"roles\": [\n" + 
                "                {\n" + 
                "                    \"label\": \"Developer\",\n" + 
                "                    \"value\": \"developer\"\n" + 
                "                },\n" + 
                "                {\n" + 
                "                    \"label\": \"Observer\",\n" + 
                "                    \"value\": \"observer\"\n" + 
                "                },\n" + 
                "                {\n" + 
                "                    \"label\": \"Admin\",\n" + 
                "                    \"value\": \"admin\"\n" + 
                "                }\n" + 
                "            ]\n" + 
                "        }\n" + 
                "    }\n" + 
                "}"));

    }
    
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
