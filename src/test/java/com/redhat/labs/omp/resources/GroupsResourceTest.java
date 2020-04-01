package com.redhat.labs.omp.resources;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class GroupsResourceTest {
//    @Test
//    public void testCreateGroupStructure() throws InterruptedException {
//        CreateResidencyGroupStructure group = new CreateResidencyGroupStructure();
//        group.projectName = System.currentTimeMillis() + "quarkus-project-name";
//        group.customerName = "quarkus-customer-name";
//        JsonbConfig config = new JsonbConfig().withPropertyNamingStrategy(
//            PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
//
//        String json = JsonbBuilder.create(config).toJson(group);
//
//        GitLabCreateProjectResponse gitLabCreateProjectResponse = given()
//                .when()
//                .contentType(ContentType.JSON)
//                .body(json)
//                .post("/api/groups")
//                .then()
//                .extract()
//                .as(GitLabCreateProjectResponse.class);
//
//
//        assertNotNull(gitLabCreateProjectResponse.id);
//    }
}