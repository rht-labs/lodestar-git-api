package com.redhat.labs.omp.models;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngagementUser {

    @NotBlank
    @JsonbProperty("first_name")
    private String firstName;
    @NotBlank
    @JsonbProperty("last_name")
    private String lastName;
    @NotBlank
    @JsonbProperty("email")
    private String email;
    @NotBlank
    @JsonbProperty("role")
    private String role;
    @JsonbTransient
    private String username;
    @JsonbTransient
    private String group;
    @JsonbTransient
    private String expirationDate;

    public String getUsername() {
        return email.substring(email.indexOf("@"));
    }

}
