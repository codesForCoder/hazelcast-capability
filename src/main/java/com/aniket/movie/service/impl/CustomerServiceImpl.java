package com.aniket.movie.service.impl;

import com.aniket.movie.dto.Customer;
import com.aniket.movie.entity.CustomerEntity;
import com.aniket.movie.entity.CustomerEntityProjection;
import com.aniket.movie.eventprocessor.CustomerUpdateEvent;
import com.aniket.movie.repository.CustomerRepository;
import com.aniket.movie.response.CustomerListResponse;
import com.aniket.movie.service.CustomerService;
import com.aniket.movie.util.ApplicationUtils;

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
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
  	private ApplicationEventPublisher publisher;
    
    @Autowired
    private ApplicationUtils applicationUtils;

    @Override
    public CustomerListResponse findAllCustomer(int limit, int page) {
        Pageable customersSortedByName =
                PageRequest.of(page, limit);
        Page<CustomerEntityProjection> customerEntities = customerRepository.findByCustomerIdNotNull(customersSortedByName);
        ModelMapper modelMapper = new ModelMapper();
        List<Customer> customers = customerEntities.getContent().stream().map(customerEntity -> modelMapper.map(customerEntity, Customer.class)).filter(cust->cust.getIsActive()==1).collect(Collectors.toList());
        CustomerListResponse customerResponse = new CustomerListResponse();
        customerResponse.setCustomerList(customers);
        customerResponse.setCurrentPage((long) page);
        customerResponse.setTotalPages((long) customerEntities.getTotalPages());
        customerResponse.setTotalElements(customerEntities.getTotalElements());
        customerResponse.setPageSize((long) limit);
        return customerResponse;
    }
    @Override
    @Async
    public void publishAllContracts() {
    	 Pageable idsForCaching =PageRequest.of(0, 10);
         Page<CustomerEntityProjection> customerEntities = customerRepository.findByCustomerIdNotNull(idsForCaching);
         while (customerEntities.hasContent()) {
        	 List<Integer> customers = customerEntities.getContent().stream().map(customerEntity -> customerEntity.getCustomerId()).collect(Collectors.toList());
        	 log.info("--------Data -------- {}",customers);
        	 customers.forEach(item->{
        		 Customer customer = new Customer();
        		 customer.setCustomerId(item);
        		 CustomerUpdateEvent event = new CustomerUpdateEvent(this, customer);
        		 publisher.publishEvent(event);
        	 });
        	 if(customerEntities.hasNext())
        	 {
        		 idsForCaching = customerEntities.nextPageable();
        		 customerEntities = customerRepository.findByCustomerIdNotNull(idsForCaching); 
        	 }else {
        		 break;
        	 }
         }
       
    }

    @Override
    public Customer findCustomerFromDB(int customerId) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(customerId);
        if(customerEntity.isPresent()){
            ModelMapper modelMapper = new ModelMapper();
            return modelMapper.map(customerEntity.get(),Customer.class);
        }else{
            throw new RuntimeException("Customer Id Not Found - "+customerId);
        }
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(customer.getCustomerId());
        if(customerEntity.isPresent()){
            ModelMapper modelMapper = new ModelMapper();
            CustomerEntity toBeSaved = modelMapper.map(customer, CustomerEntity.class);
            toBeSaved.setLastUpdated(new Date());
            log.info("Initial Prepared Data - {}",toBeSaved);
            CustomerEntity savedData = customerRepository.save(toBeSaved);
            log.info("Final Saved Data - {}",savedData);
            return modelMapper.map(savedData, Customer.class);
        }else{
            throw new RuntimeException("Customer Id Not Found - "+customer.getCustomerId());
        }
    }
	@Override
	public void publishCustomerUpdateForAddressChange(int addressId) {
		 Pageable idsForCaching =PageRequest.of(0, 10);
         Page<Integer> customerEntities = customerRepository.findCustomersByAddressId(idsForCaching,addressId);
         while (customerEntities.hasContent()) {
        	 List<Integer> customers = customerEntities.getContent();
        	 log.info("--------Data -------- {}",customers);
        	 customers.forEach(item->{
        		 Customer customer = new Customer();
        		 customer.setCustomerId(item);
        		 CustomerUpdateEvent event = new CustomerUpdateEvent(this, customer);
        		 publisher.publishEvent(event);
        	 });
        	 if(customerEntities.hasNext())
        	 {
        		 idsForCaching = customerEntities.nextPageable();
        		 customerEntities = customerRepository.findCustomersByAddressId(idsForCaching,addressId); 
        	 }else {
        		 break;
        	 }
         }
	}
}
