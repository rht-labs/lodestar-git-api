package com.redhat.labs.cache;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

/**
 * A utility class to hold the rediency data
 */
public class ResidencyInformation implements Serializable {

    private final String residencyYaml;

    //TODO - Change it to a more stricter object
    private final Object metadata;

    private static final long serialversionUID = 1L;

    public ResidencyInformation(String residencyYaml, Object metadata) {

        this.residencyYaml = residencyYaml;
        this.metadata = metadata;

        explodeData();
    }

    //a utlitu method to read meta data and stoe them as value for easier access
    private void explodeData() {
        logger.info("Not exploding ATM");
    }


    public String getResidencyYaml() {
        return residencyYaml;
    }

    public Object getMetadata() {
        return metadata;
    }

    public static Logger logger = LoggerFactory.getLogger(ResidencyInformation.class);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResidencyInformation that = (ResidencyInformation) o;
        return Objects.equals(residencyYaml, that.residencyYaml) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(residencyYaml, metadata);
    }
}
