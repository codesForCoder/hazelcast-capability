package com.aniket.movie.dto;

import lombok.Data;

import java.util.Date;

@Data
public class Customer {

    private Integer customerId;

    private Integer storeId;

    private String firstName;

    private String lastName;

    private String email;

    private Address address;

    private Integer isActive;

    private Date dateCreated;

    private Date lastUpdated;
}
