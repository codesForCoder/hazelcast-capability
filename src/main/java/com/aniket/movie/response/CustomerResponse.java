package com.aniket.movie.response;

import lombok.Data;

@Data
public class CustomerResponse {
    private EnvironmentDetails environmentDetails;
    private Integer customerId;

    private String firstName;

    private String lastName;

    private String email;

    private Integer isActive;

}
