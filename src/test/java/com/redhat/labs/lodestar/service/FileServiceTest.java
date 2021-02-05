package com.redhat.labs.lodestar.service;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.redhat.labs.lodestar.models.gitlab.File;
import com.redhat.labs.lodestar.models.gitlab.FileAction;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class FileServiceTest {
    
    @Inject
    FileService fileService;
    
    
    
    @Test
    void testFileAction() {
        Assertions.assertEquals("create", FileAction.CREATE.toString());
        Assertions.assertEquals("delete", FileAction.DELETE.toString());
        Assertions.assertEquals("update", FileAction.UPDATE.toString());
    }
    
    
    @Test
    void testGetFile404() {
        Optional<File> fileNotFound = fileService.getFileAllow404(7, "404.error");
        
        Assertions.assertFalse(fileNotFound.isPresent());
    }
    
    @Test
    void testGetFile() {
        Optional<File> fileNotFound = fileService.getFile(7, "404.error");
        
        Assertions.assertFalse(fileNotFound.isPresent());
    }
    
    @Test
    void testGetFile500() {
        Assertions.assertThrows(WebApplicationException.class, () -> {
            fileService.getFile(7, "500.error");
        });
    }
    
    @Test
    void deleteFile() {
        Optional<File> deletedFile = fileService.deleteFile(7, "engagement.json");
        Assertions.assertTrue(deletedFile.isPresent());
    }
    
    @Test
    void deleteFileNotExist() {
        Optional<File> deletedFile = fileService.deleteFile(7, "nonexistent.rht");
        Assertions.assertFalse(deletedFile.isPresent());
    }
    
    @Test
    void updateFile() {
        Optional<File> updatedFile = fileService.updateFile(7, "update.file", File.builder().filePath("update.file").content("hi").build());
        Assertions.assertTrue(updatedFile.isPresent());
    }
    
    @Test
    void updateFileNotExist() {
        Optional<File> updatedFile = fileService.updateFile(7, "nonexistent.rht", File.builder().filePath("update.file").content("hi").build());
        Assertions.assertFalse(updatedFile.isPresent());
    }
    
    @Test
    void createFile() {
        Optional<File> updatedFile = fileService.createFile(7, "create.file", File.builder().filePath("create.file").content("hi").build());
        Assertions.assertTrue(updatedFile.isPresent());
    }
    
    @Test
    void createFileNotExist() {
        Optional<File> updatedFile = fileService.createFile(7, "nonexistent.rht", File.builder().filePath("update.file").content("hi").build());
        Assertions.assertFalse(updatedFile.isPresent());
    }

}
