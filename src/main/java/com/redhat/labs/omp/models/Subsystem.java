package com.redhat.labs.omp.models;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subsystem {

    private String name;
    private String status;
    private String state;
    private String info;
    private String updated;
    private List<Message> messages;
    private List<Map<String, Object>> accessUrls;

}
