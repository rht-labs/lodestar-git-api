package com.redhat.labs.omp.models;

public class Engagement {

    public int id;
    public String customerName;
    public String projectName;
    public String description;
    public String location;
    public String startDate;
    public String endDate;
    public String archiveDate;
    public String engagementLeadName;
    public String engagementLeadEmail;
    public String technicalLeadName;
    public String technicalLeadEmail;
    public String customerContactName;
    public String customerContactEmail;
    public String ocpCloudProviderName;
    public String ocpCloudProviderRegion;
    public String ocpVersion;
    public String ocpSubDomain;
    public String ocpPersistentStorageSize;
    public String ocpClusterSize;

    public Engagement () {}

    public String toString() {
        String engagement = "Engagement (%d) Customer: %s Project: %s Description: %s Location: %s Start Date: %s"
                + " End Date: %s Archive Date: %s + Engagement Lead %s (%s) Tech Lead %s (%s) Cust Contact %s (%s)"
                + "OpenShift: Cloud Provider: %s Region: %s Version: %s Sub Domain: %s Storage: %s + Cluster size: %s";

        return String.format(engagement, id, customerName, projectName, description, location, startDate,
                endDate, archiveDate, engagementLeadName, engagementLeadEmail, technicalLeadName, technicalLeadEmail,
                customerContactName, customerContactEmail, ocpCloudProviderName, ocpCloudProviderRegion,
                ocpVersion, ocpSubDomain, ocpPersistentStorageSize, ocpClusterSize);
    }
}
