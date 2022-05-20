package com.aniket.movie.service;

import com.aniket.movie.dto.Customer;
import com.aniket.movie.response.CustomerListResponse;

public interface CustomerService {

    CustomerListResponse findAllCustomer(int limit , int page);
    void publishAllContracts();
    Customer findCustomerFromDB(int customerId);
    void publishCustomerUpdateForAddressChange(int addressId);
    Customer updateCustomer(Customer customer);
}
