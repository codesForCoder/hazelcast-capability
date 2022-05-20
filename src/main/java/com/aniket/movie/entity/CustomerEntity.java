package com.aniket.movie.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

import static com.aniket.movie.constant.TableName.CUSTOMER;

@Data
@Entity
@Table(name = CUSTOMER)
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id" ,insertable = false, updatable = false)
    private Integer customerId;
    @Column(name = "store_id" ,insertable = false, updatable = false)
    private Integer storeId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;

    @OneToOne
    @JoinColumn(name = "address_id" ,insertable = false, updatable = false)
    private AddressEntity address;

    @Column(name = "active")
    private Integer isActive;
    @Column(name = "create_date" ,insertable = false, updatable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, fallbackPatterns = { "M/d/yy", "dd.MM.yyyy" })
    private Date dateCreated;
    @Column(name = "last_update")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, fallbackPatterns = { "M/d/yy", "dd.MM.yyyy" })
    private Date lastUpdated;
}
