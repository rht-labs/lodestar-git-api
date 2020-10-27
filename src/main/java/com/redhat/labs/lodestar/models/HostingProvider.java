package com.redhat.labs.lodestar.models;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

import com.redhat.labs.lodestar.models.gitlab.Commit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Engagement {
  private String id;

  private String additionalDetails;

  private String ocpCloudProviderName;

  private String ocpCloudProviderRegion;

  private String ocpPersistentStorageSize;

  private String ocpSubDomain;

  private String ocpVersion;
}