package com.aniket.movie.service;

import com.aniket.movie.dto.Address;
import com.aniket.movie.dto.Customer;

import java.util.List;

public interface AddressService {

    Address updateAddress(Address address);
    Address findAddressFromDB(int addressId);
	void publishAllAddresses();
}
