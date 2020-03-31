package com.redhat.labs.omp.models.filesmanagement;

import org.junit.jupiter.api.Test;

import com.redhat.labs.omp.models.gitlab.request.CommitMultipleFilesInRepsitoryRequest;
import com.redhat.labs.omp.models.gitlab.request.CreateCommitFileRequest;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import static org.junit.jupiter.api.Assertions.*;

class CommitMultipleFilesInRepsitoryRequestTest {


    @Test
    public void testJsonSerilisation(){
        CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequest = new CommitMultipleFilesInRepsitoryRequest();
        commitMultipleFilesInRepsitoryRequest.addFileRequest(new CreateCommitFileRequest("file1", FILE1));
        commitMultipleFilesInRepsitoryRequest.addFileRequest(new CreateCommitFileRequest("folder/file2", FILE2));

        Jsonb jsonb = JsonbBuilder.create();
        String result = jsonb.toJson(commitMultipleFilesInRepsitoryRequest);
        CommitMultipleFilesInRepsitoryRequest commitMultipleFilesInRepsitoryRequestReborn =  jsonb.fromJson(result, CommitMultipleFilesInRepsitoryRequest.class);


        assertEquals(2, commitMultipleFilesInRepsitoryRequestReborn.actions.length);
        assertEquals(FILE1, commitMultipleFilesInRepsitoryRequest.getActions()[0].getContent());


    }


    private final static String FILE1 = "some data";
    private final static String FILE2 = "some data for file two";



}