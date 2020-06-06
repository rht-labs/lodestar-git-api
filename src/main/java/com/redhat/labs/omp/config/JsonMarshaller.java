package com.redhat.labs.omp.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.quarkus.runtime.StartupEvent;

/**
 * Used converting String to Objects (non-request, non-response)
 * @author mcanoy
 *
 */
@ApplicationScoped
public class JsonMarshaller {
    public static Logger LOGGER = LoggerFactory.getLogger(JsonMarshaller.class);

    private Jsonb jsonb;
    
    private ObjectMapper om = new ObjectMapper(new YAMLFactory());
    
    void onStart(@Observes StartupEvent event) { 
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true)
                .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        jsonb = JsonbBuilder.create(config);
        
    }
    
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
        
        return null;
    }
    
    public <T> List<T> fromYaml(String yamlContent, Class<T> clazz) {
        JavaType type = om.getTypeFactory().constructCollectionType(List.class, clazz);
        
        try {
            return om.readValue(yamlContent, type);
        } catch (IOException e) {
            LOGGER.error(String.format("Found but unable to map file %s", yamlContent), e);
        }
        
        return null;
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
