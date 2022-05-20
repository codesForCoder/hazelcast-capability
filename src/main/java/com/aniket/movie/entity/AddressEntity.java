package com.aniket.movie.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.util.Date;

import static com.aniket.movie.constant.TableName.ADDRESS;

@Data
@Entity
@Table(name = ADDRESS)
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "address_id" ,insertable = false, updatable = false)
    private Integer addressId;
    @Column(name = "address")
    private String addressLine1;
    @Column(name = "address2")
    private String addressLine2;
    @Column(name = "district")
    private String district;
    @Column(name = "city_id" ,insertable = false, updatable = false)
    private Integer cityId;
    @Column(name = "postal_code")
    private String postalCode;
    @Column(name = "phone")
    private String phoneNumber;
    @Column(name = "last_update")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, fallbackPatterns = { "M/d/yy", "dd.MM.yyyy" })
    private Date lastUpdated;

}
