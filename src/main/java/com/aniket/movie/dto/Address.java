package com.aniket.movie.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
public class Address {

    private Integer addressId;

    private String addressLine1;

    private String addressLine2;

    private String district;

    private Integer cityId;

    private String postalCode;

    private String phoneNumber;

    private Date lastUpdated;
}
