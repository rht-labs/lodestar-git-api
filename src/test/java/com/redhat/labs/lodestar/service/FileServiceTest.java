package com.redhat.labs.lodestar.service;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

import com.redhat.labs.lodestar.models.gitlab.File;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FileServiceTest {
    
    @Inject
    FileService fileService;
    
    
    @Test
    public void testGetFile404() {
        Optional<File> fileNotFound = fileService.getFileAllow404(7, "404.error");
        
        Assert.assertFalse(fileNotFound.isPresent());
    }
    
    @Test
    public void testGetFile() {
        Optional<File> fileNotFound = fileService.getFile(7, "404.error");
        
        Assert.assertFalse(fileNotFound.isPresent());
    }
    
    @Test
    public void testGetFile500() {
        Assertions.assertThrows(WebApplicationException.class, () -> {
            fileService.getFile(7, "500.error");
        });
    }
    
    @Test
    public void deleteFile() {
        Optional<File> deletedFile = fileService.deleteFile(7, "engagement.json");
        Assert.assertTrue(deletedFile.isPresent());
    }
    
    @Test
    public void deleteFileNotExist() {
        Optional<File> deletedFile = fileService.deleteFile(7, "nonexistent.rht");
        Assert.assertFalse(deletedFile.isPresent());
    }
    
    @Test
    public void updateFile() {
        Optional<File> updatedFile = fileService.updateFile(7, "update.file", File.builder().filePath("update.file").content("hi").build());
        Assert.assertTrue(updatedFile.isPresent());
    }
    
    @Test
    public void updateFileNotExist() {
        Optional<File> updatedFile = fileService.updateFile(7, "nonexistent.rht", File.builder().filePath("update.file").content("hi").build());
        Assert.assertFalse(updatedFile.isPresent());
    }
    
    @Test
    public void createFile() {
        Optional<File> updatedFile = fileService.createFile(7, "create.file", File.builder().filePath("create.file").content("hi").build());
        Assert.assertTrue(updatedFile.isPresent());
    }
    
    @Test
    public void createFileNotExist() {
        Optional<File> updatedFile = fileService.createFile(7, "nonexistent.rht", File.builder().filePath("update.file").content("hi").build());
        Assert.assertFalse(updatedFile.isPresent());
    }

}
