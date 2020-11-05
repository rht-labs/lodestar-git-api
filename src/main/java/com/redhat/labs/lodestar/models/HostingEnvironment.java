package com.redhat.labs.lodestar.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostingEnvironment {
  private String id;

  private String additionalDetails;

  private String ocpCloudProviderName;

  private String ocpCloudProviderRegion;

  private String ocpPersistentStorageSize;

  private String ocpSubDomain;

  private String ocpVersion;

  private String environmentName;
}