package com.redhat.labs.omp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDetail {

    private String id;
    private String name;
    private String type;
    private String path;
    private String mode;

}