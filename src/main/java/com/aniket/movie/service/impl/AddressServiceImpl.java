package com.aniket.movie.service.impl;

import com.aniket.movie.dto.Address;
import com.aniket.movie.dto.Customer;
import com.aniket.movie.entity.AddressEntity;
import com.aniket.movie.entity.AddressEntityProjection;
import com.aniket.movie.entity.CustomerEntity;
import com.aniket.movie.entity.CustomerEntityProjection;
import com.aniket.movie.eventprocessor.AddressUpdateEvent;
import com.aniket.movie.eventprocessor.CustomerUpdateEvent;
import com.aniket.movie.repository.AddressRepository;
import com.aniket.movie.repository.CustomerRepository;
import com.aniket.movie.response.AddressListResponse;
import com.aniket.movie.response.CustomerListResponse;
import com.aniket.movie.service.AddressService;
import com.aniket.movie.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
  	private ApplicationEventPublisher publisher;

    @Override
    public Address updateAddress(Address address) {
        Optional<AddressEntity> addressEntity = addressRepository.findById(address.getAddressId());
        if(addressEntity.isPresent()){
            ModelMapper modelMapper = new ModelMapper();
            AddressEntity toBeSaved = modelMapper.map(address, AddressEntity.class);
            toBeSaved.setLastUpdated(new Date());
            log.info("Initial Prepared Data - {}",toBeSaved);
            AddressEntity savedData = addressRepository.save(toBeSaved);
            log.info("Final Saved Data - {}",savedData);
            return modelMapper.map(savedData, Address.class);
        }else{
            throw new RuntimeException("Customer Id Not Found - "+address.getAddressId());
        }
    }

    @Override
    public Address findAddressFromDB(int addressId) {
        Optional<AddressEntity> addressEntity = addressRepository.findById(addressId);
        if(addressEntity.isPresent()){
            ModelMapper modelMapper = new ModelMapper();
            return modelMapper.map(addressEntity.get(),Address.class);
        }else{
            throw new RuntimeException("Address Id Not Found - "+addressId);
        }
    }

	@Override
	@Async
	public void publishAllAddresses() {
		 Pageable idsForCaching =PageRequest.of(0, 10);
         Page<AddressEntityProjection> addressEntity = addressRepository.findByAddressIdNotNull(idsForCaching);
         while (addressEntity.hasContent()) {
        	 List<Integer> addresses = addressEntity.getContent().stream().map(customerEntity -> customerEntity.getAddressId()).collect(Collectors.toList());
        	 log.info("--------Data -------- {}",addresses);
        	 addresses.forEach(item->{
        		 Address address = new Address();
        		 address.setAddressId(item);
        		 AddressUpdateEvent event = new AddressUpdateEvent(this, address);
        		 publisher.publishEvent(event);
        	 });
        	 if(addressEntity.hasNext())
        	 {
        		 idsForCaching = addressEntity.nextPageable();
        		 addressEntity = addressRepository.findByAddressIdNotNull(idsForCaching); 
        	 }else {
        		 break;
        	 }
         }
		
	}

    @Override
    public AddressListResponse findAllAddress(int limit, int page) {
        Pageable addressesSortedByName =
                PageRequest.of(page, limit);
        Page<AddressEntityProjection> addressEntityProjections = addressRepository.findByAddressIdNotNull(addressesSortedByName);
        ModelMapper modelMapper = new ModelMapper();
        List<Address> addresses = addressEntityProjections.getContent().stream().map(addressEntityProjection -> modelMapper.map(addressEntityProjection, Address.class)).collect(Collectors.toList());
        AddressListResponse addressListResponse = new AddressListResponse();
        addressListResponse.setAddressList(addresses);
        addressListResponse.setCurrentPage((long) page);
        addressListResponse.setTotalPages((long) addressEntityProjections.getTotalPages());
        addressListResponse.setTotalElements(addressEntityProjections.getTotalElements());
        addressListResponse.setPageSize((long) limit);
        return addressListResponse;
    }
}
