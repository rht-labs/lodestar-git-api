package com.redhat.labs.lodestar.models;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigMap {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigMap.class);

    private Path path;
    private String filePath;
    @Builder.Default
    private Optional<String> content = Optional.empty();
    private long lastModifiedTime;

    /**
     * Returns true if the content has been loaded from the configured file.
     * Otherwise, false.
     * 
     * @return
     */
    public boolean updateMountedFile() {

        if (null == path) {
            path = Paths.get(filePath);
        }

        if (Files.isReadable(path) && isModified()) {

            try {
                content = Optional.of(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
                return true;
            } catch (IOException e) {
                LOGGER.error("Error updating mounted file %{} {} {} ", lastModifiedTime, path, filePath);
                content = Optional.empty();
            }

        }

        return false;

    }

    /**
     * Returns true if the file has been modified since the last load. Otherwise,
     * false.
     * 
     * @return
     */
    private boolean isModified() {

        FileTime fileTime;
        try {
            fileTime = Files.getLastModifiedTime(path);
            if (fileTime.toMillis() > lastModifiedTime) {
                lastModifiedTime = fileTime.toMillis();
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Error calculating isModified %{} {}", lastModifiedTime, path);
            return false;
        }

        return false;
    }

}
