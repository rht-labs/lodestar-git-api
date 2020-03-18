package com.redhat.labs.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

/**
 * A utility class to hold the residency data
 */
public class ResidencyInformation implements Serializable {
	public static Logger logger = LoggerFactory.getLogger(ResidencyInformation.class);

    private final String residencyYaml;

    //TODO - Change it to a more stricter object
    private final Object metadata;

    private static final long serialVersionUID = 1L;

    public ResidencyInformation(String residencyYaml, Object metadata) {

        this.residencyYaml = residencyYaml;
        this.metadata = metadata;

        explodeData();
    }

    //a utlity method to read meta data and store them as value for easier access
    private void explodeData() {
        logger.info("Not exploding ATM");
    }


    public String getResidencyYaml() {
        return residencyYaml;
    }

    public Object getMetadata() {
        return metadata;
    }


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
