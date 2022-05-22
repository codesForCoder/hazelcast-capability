package com.aniket.movie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MMM-dd hh:mm:ss aa")

    private Date dateCreated;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MMM-dd hh:mm:ss aa")

    private Date lastUpdated;
}
