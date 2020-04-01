package com.redhat.labs.omp;

import io.quarkus.test.junit.QuarkusTest;


@QuarkusTest
public class TemplateResuorceIT {


/*    @Test

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

    }*/

//    @Test
//    public void testCommitMultipleFiles() {
//        CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequest = new CommitMultipleFilesInRepsitoryRequest();
//        commitMultipleFilesInRepsitoryRequest.addFileRequest(new CreateCommitFileRequest("file1", FILE1));
//        commitMultipleFilesInRepsitoryRequest.addFileRequest(new CreateCommitFileRequest("folder/file2", FILE2));
//        templateResource.commitMultipleFilesToRepository(gitProjectId, commitMultipleFilesInRepsitoryRequest);
//
//
//    }
//
//    @Inject
//    TemplateResource templateResource;
//
//    private final static String FILE1 = "some data";
//    private final static String FILE2 = "some data for file two";
//
//
//    public static final String NAMESPACE = "residencies";
//    public static final String PROJECT_NAME = "quarkus-integration-test-new-residency";
//
//    @BeforeEach
//    public void init() {
//        try {
//            createProject(false);
//        } catch (Throwable e) {
//            // ignored by design
//        }
//    }
//
//    @AfterEach
//    public void teardown() {
//        try {
//            deleteProject(false);
//            Thread.sleep(1000);
//        } catch (Throwable e) {
//            // ignored by design
//        }
//    }
//
//
//    private static void deleteProject(boolean doAssert) {
//        ValidatableResponse r = given()
//                .when()
//                .pathParam("project_id", NAMESPACE + "/" + PROJECT_NAME)
//                .delete("/api/projects/{project_id}")
//                .then();
//
//        if (doAssert) {
//            r.statusCode(200);
//        }
//    }
//
//    private static void createProject(boolean doAssert) {
//        ValidatableResponse r = given()
//                .when()
//                .contentType(ContentType.JSON)
//                .body("{ \"residency_name\" : \"" + PROJECT_NAME + "\" }")
//                .post("/api/projects")
//                .then()
//                ;
//
//
//        gitProjectId = r.extract().as(GitLabCreateProjectResponse.class).id;
//
//
//
//        if (doAssert) {
//            r.statusCode(200);
//        }
//    }
//
//    private static Integer gitProjectId;

}
