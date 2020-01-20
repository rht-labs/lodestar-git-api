package com.redhat.labs.omp.models;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResidencyTest {

    @Test
    void toMap() throws IllegalAccessException {
        Residency residency = new Residency();
        residency.openShiftVersion = "v4.3";
        residency.openShiftClusterSize = "medium";

        Map<String, Object> fieldValues = residency.toMap();

        assertEquals("v4.3", fieldValues.get("openShiftVersion"));

    }
}