package com.redhat.labs.omp.models;

import java.io.Serializable;

/**
 * This model encapuslates the fields that are required to create project group structure.
 * {@see GroupResource}
 */
public class CreateResidencyGroupStructure implements Serializable {

    public String customerName;
    public String projectName;

}
