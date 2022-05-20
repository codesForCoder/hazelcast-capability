package com.aniket.movie.request;

import lombok.Data;

import java.util.Date;

@Data
public class AddressRequest {



    private String addressLine1;

    private String addressLine2;

    private String district;

    private Integer cityId;

    private String postalCode;

    private String phoneNumber;


}
