package com.redhat.labs.omp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreationDetails {

    private String createdByUser;
    private String createdByEmail;
    private String createdOn;

}
