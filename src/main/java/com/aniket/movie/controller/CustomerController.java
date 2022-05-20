package com.aniket.movie.controller;


import com.aniket.movie.constant.CacheContext;
import com.aniket.movie.dto.Customer;
import com.aniket.movie.eventprocessor.CustomerUpdateEvent;
import com.aniket.movie.request.CustomerRequest;
import com.aniket.movie.response.CustomerListResponse;
import com.aniket.movie.response.CustomerResponse;
import com.aniket.movie.service.CustomerService;
import com.aniket.movie.util.ApplicationUtils;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static com.aniket.movie.constant.RestEndpoint.CUSTOMER_ENDPOINT;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(CUSTOMER_ENDPOINT)
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    
    @Autowired
  	private ApplicationEventPublisher publisher;
    
    @Autowired
    private ApplicationUtils applicationUtils;

    @GetMapping(params = {"page" , "limit" ,"noCache"})
    public CustomerListResponse getCustomers(@RequestParam int page , @RequestParam int limit , @RequestParam(defaultValue = "false" , required = false)String  noCache){
        log.info("Fetching Customer for Page --- {} with Page Size - {}" , page , limit);
        CustomerListResponse customers =customerService.findAllCustomer(limit,page);
        log.info("is Data Need to be treived from Database - Mandetory ? - {}",noCache);
        log.info("Enrich Customer Payload wither from Cache or DB");
        customers.setCustomerList(customers.getCustomerList().stream().map(cust->{
        	return  getCustomer(cust.getCustomerId(),noCache);
        }).collect(Collectors.toList()));
        return customers;
    }
    
    @PostMapping
    public ResponseEntity<String> initCache(){
        log.info("Starting Customer Payload Cache Building");
        customerService.publishAllContracts();
        return new ResponseEntity<String>("Cache Building Started ....",HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/{id}" , params = {"noCache"})
    public Customer getCustomer(@PathVariable("id") int customerId , @RequestParam(defaultValue = "false" , required = false)String  noCache){
        log.info("Fetching Customer for  id -- {}" , customerId );
        log.info("is Data Need to be treived from Database - Mandetory ? - {}",noCache);
        Boolean isFromDB = Boolean.valueOf(noCache);
        Customer customer;
        String cache = applicationUtils.getCache(String.valueOf(customerId), CacheContext.CUSTOMER);
        if(StringUtils.hasText(cache) && !isFromDB) {
        	log.info("Raw Data - {}" , cache);
        	customer = new Gson().fromJson(cache,Customer.class);
        }else {
        	customer=customerService.findCustomerFromDB(customerId);
        }
         
        return customer;
    }
    @PutMapping(path = "/{id}")
    public CustomerResponse updateCustomer(@PathVariable("id") int customerId , @RequestBody CustomerRequest customerRequest){
        log.info("Updating Data From Customer ID --- {} with Payload - {}" , customerId , customerRequest);
        ModelMapper modelMapper = new ModelMapper();
        Customer customer =  modelMapper.map(customerRequest, Customer.class);
        customer.setCustomerId(customerId);
        Customer updatedCustomer =customerService.updateCustomer(customer);
        CustomerUpdateEvent event = new CustomerUpdateEvent(this, updatedCustomer);
		publisher.publishEvent(event);
        return modelMapper.map(updatedCustomer, CustomerResponse.class);

    }
    
    
    
    

}
