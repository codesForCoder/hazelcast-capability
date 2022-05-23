package com.aniket.movie.service;

import com.aniket.movie.dto.Address;
import com.aniket.movie.dto.Customer;
import com.aniket.movie.response.AddressListResponse;
import com.aniket.movie.response.CustomerListResponse;

import java.util.List;

public interface AddressService {
    AddressListResponse findAllAddress(int limit , int page);
    Address updateAddress(Address address);
    Address findAddressFromDB(int addressId);
	void publishAllAddresses();
}
