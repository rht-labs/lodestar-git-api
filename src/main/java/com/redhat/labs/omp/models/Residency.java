package com.redhat.labs.omp.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@RegisterForReflection
public class Residency implements Serializable {
    @JsonbProperty("id")
    public Integer id;

    @JsonbProperty("customer_name")
    public String customerName;

    @JsonbProperty("project_name")
    public String projectName;

    @JsonbProperty("description")
    public String description;

    @JsonbProperty("location")
    public String location;

    @JsonbProperty("start_date")
    public String startDate;

    @JsonbProperty("end_date")
    public String endDate;

    @JsonbProperty("archive_date")
    public String archiveDate;

    @JsonbProperty("engagement_lead_name")
    public String engagementLeadName;

    @JsonbProperty("engagement_lead_email")
    public String engagementLeadEmail;

    @JsonbProperty("technical_lead_name")
    public String technicalLeadName;

    @JsonbProperty("technical_lead_email")
    public String technicalLeadEmail;

    @JsonbProperty("customer_contact_name")
    public String customerContactName;

    @JsonbProperty("customer_contact_email")
    public String customerContactEmail;

    @JsonbProperty("ocp_cloud_provider_name")
    public String openShiftCloudProviderName;

    @JsonbProperty("ocp_cloud_provider_region")
    public String openShiftCloudProviderRegion;

    @JsonbProperty("ocp_version")
    public String openShiftVersion;

    @JsonbProperty("ocp_sub_domain")
    public String openShiftSubDomain;

    @JsonbProperty("ocp_persistent_storage_size")
    public String openShiftPersistentStorageSize;

    @JsonbProperty("ocp_cluster_size")
    public String openShiftClusterSize;

    public Map<String, Object> toMap() {
        if (fieldsMap != null) {
            return fieldsMap;
        }

        fieldsMap = new HashMap<>();

        try {
            Class clazz = this.getClass();
            Field[] fieldsOfThisClass = clazz.getDeclaredFields();
            for (Field oneField : fieldsOfThisClass) {
                if (oneField.isAnnotationPresent(JsonbProperty.class)) {
                    JsonbProperty annotation = oneField.getAnnotation(JsonbProperty.class);
                    fieldsMap.put(annotation.value(), oneField.get(this));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return fieldsMap;
    }

    // lazy load fields
    private Map<String, Object> fieldsMap;
}
