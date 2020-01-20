package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

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
}
