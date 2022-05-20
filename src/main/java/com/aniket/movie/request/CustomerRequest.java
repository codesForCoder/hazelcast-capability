package com.aniket.movie.request;

import com.aniket.movie.dto.Address;
import lombok.Data;

import java.util.Date;

@Data
public class CustomerRequest {

    private String firstName;

    private String lastName;

    private String email;

    private Integer isActive;

}
