package com.redhat.labs.lodestar.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Lists;

import lombok.Setter;

/**
 * Used converting String to Objects (non-request, non-response)
 * @author mcanoy
 *
 */
@ApplicationScoped
public class JsonMarshaller {
    public static final Logger LOGGER = LoggerFactory.getLogger(JsonMarshaller.class);

    @Inject
    @Setter
    Jsonb jsonb;
    
    private ObjectMapper om = new ObjectMapper(new YAMLFactory());

    /**
     * Reads a file from the system and returns the contents transformed into a new object. Must be a list. If this fails
     * for any IOException or the file is not readable it will return null 
     * @param <T>
     * @param yamlFile The path to the file on disk
     * @param clazz The class that will be return as a list
     * @return A list!
     */
    public <T> List<T> fromYamlFile(String yamlFile, Class<T> clazz) {
        
        Path path = Paths.get(yamlFile);
        
        if(Files.isReadable(path)) {
            LOGGER.debug("Loading config file {}", yamlFile);
            String yamlFileContent;
            try {
                yamlFileContent = new String(Files.readAllBytes(path));
                return fromYaml(yamlFileContent, clazz);
            } catch (IOException e) {
                LOGGER.error(String.format("Found but unable to read file %s", yamlFile), e);
            }
        } 
        
        return Lists.newArrayList();
    }
    
    public <T> List<T> fromYaml(String yamlContent, Class<T> clazz) {
        JavaType type = om.getTypeFactory().constructCollectionType(List.class, clazz);
        
        try {
            return om.readValue(yamlContent, type);
        } catch (IOException e) {
            LOGGER.error(String.format("Found but unable to map file %s", yamlContent), e);
        }
        
        return Lists.newArrayList();
    }
    
    
    public <T> String toJson(T object) {
        return jsonb.toJson(object);
    }
    
    public <T> T fromJson(String json, Class<T> type) {
        
        return jsonb.fromJson(json, type);
    }
    
    public <T> T fromJson(String json, Type type) {
        return jsonb.fromJson(json, type);
    }

}
