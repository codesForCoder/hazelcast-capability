package com.aniket.movie.response;

import lombok.Data;

import java.util.Date;

@Data
public class AddressResponse {

    private EnvironmentDetails environmentDetails;

    private Integer addressId;

    private String addressLine1;

    private String addressLine2;

    private String district;

    private Integer cityId;

    private String postalCode;

    private String phoneNumber;

    private Date lastUpdated;
}
