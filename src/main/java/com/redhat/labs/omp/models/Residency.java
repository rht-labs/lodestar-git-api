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
}
