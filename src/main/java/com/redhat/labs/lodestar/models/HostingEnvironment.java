package com.redhat.labs.lodestar.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HostingEnvironment extends EngagementAttribute {

    private String additionalDetails;

    private String ocpCloudProviderName;

    private String ocpCloudProviderRegion;

    private String ocpPersistentStorageSize;

    private String ocpSubDomain;

    private String ocpVersion;

    private String environmentName;

    private String ocpClusterSize;
    
    private String region;
}