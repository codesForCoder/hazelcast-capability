package com.aniket.movie.controller;


import com.aniket.movie.constant.CacheContext;
import com.aniket.movie.constant.DataSource;
import com.aniket.movie.dto.Customer;
import com.aniket.movie.eventprocessor.CustomerUpdateEventAsync;
import com.aniket.movie.eventprocessor.CustomerUpdateEventInSync;
import com.aniket.movie.request.CustomerRequest;
import com.aniket.movie.request.SearchListRequest;
import com.aniket.movie.response.*;
import com.aniket.movie.service.CustomerService;
import com.aniket.movie.util.ApplicationUtils;
import com.configcat.ConfigCatClient;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
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

    @Autowired
    private ConfigCatClient client;

    @GetMapping(params = {"page" , "limit" ,"source"})
    public CustomerListResponse getCustomers(@RequestParam int page , @RequestParam int limit , @RequestParam(defaultValue = "LOCAL_CACHE" , required = false)DataSource  dataSrc){
        log.info("Fetching Customer for Page --- {} with Page Size - {}" , page , limit);
       if(dataSrc==null) {
           dataSrc = DataSource.fromValue("LOCAL_CACHE");
       }
        CustomerListResponse customers =customerService.findAllCustomer(limit,page);
        log.info("is Data Need to be treived from Database - Mandetory ? - {}", dataSrc);
        log.info("Enrich Customer Payload wither from Cache or DB");

        DataSource finalDataSrc = dataSrc;
        customers.setCustomerList(customers.getCustomerList().stream().map(cust->{
            Customer customer = getCustomer(cust.getCustomerId(), finalDataSrc);
            customer.setEnvironmentDetails(null);
            return customer;
        }).collect(Collectors.toList()));
        customers.setEnvironmentDetails(applicationUtils.getCurrentEnv());
        return customers;
    }
    
    @PostMapping
    public ResponseEntity<String> initCache(){
        log.info("Starting Customer Payload Cache Building");
        customerService.publishAllContracts();
        return new ResponseEntity<String>("Cache Building Started at Env :: "+applicationUtils.getCurrentEnv(),HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/{id}" , params = {"source"})
    public Customer getCustomer(@PathVariable("id") int customerId , @RequestParam(defaultValue = "LOCAL_CACHE" , required = false)DataSource  dataSrc){
        log.info("Fetching Customer for  id -- {}" , customerId );
        if(dataSrc==null) {
            dataSrc = DataSource.fromValue("LOCAL_CACHE");
        }
        log.info("is Data Need to be rtreived from Database - Mandetory ? - {}", dataSrc);
        Customer customer;
        String cache=null;
        if(DataSource.LOCAL_CACHE.equals(dataSrc)){
            cache = applicationUtils.getCacheLocal(String.valueOf(customerId), CacheContext.CUSTOMER);
        }else if(DataSource.REMOTE_CACHE.equals(dataSrc)){
            cache = applicationUtils.getCacheRemote(String.valueOf(customerId), CacheContext.CUSTOMER);
        }

        if(StringUtils.hasText(cache) && !DataSource.DB.equals(dataSrc)) {
        	log.info("Raw Data - {}" , cache);
            Gson gson = new GsonBuilder()
                    .setDateFormat( "yyyy-MMM-dd hh:mm:ss aa").create();
            customer = gson.fromJson(cache,Customer.class);
        }else {
        	customer=customerService.findCustomerFromDB(customerId);
        }
         customer.setEnvironmentDetails(applicationUtils.getCurrentEnv());
        return customer;
    }
    @PutMapping(path = "/{id}")
    public CustomerResponse updateCustomer(@PathVariable("id") int customerId , @RequestBody CustomerRequest customerRequest){
        log.info("Updating Data From Customer ID --- {} with Payload - {}" , customerId , customerRequest);
        ModelMapper modelMapper = new ModelMapper();
        Customer customer =  modelMapper.map(customerRequest, Customer.class);
        customer.setCustomerId(customerId);
        Customer updatedCustomer =customerService.updateCustomer(customer);
        boolean consistency_level_is_full = client.getValue(Boolean.class, "consistency_level_is_full", false);
        if(consistency_level_is_full){
            CustomerUpdateEventInSync event = new CustomerUpdateEventInSync(this, updatedCustomer);
            publisher.publishEvent(event);
        }else{
            CustomerUpdateEventAsync event = new CustomerUpdateEventAsync(this, updatedCustomer);
            publisher.publishEvent(event);
        }

        CustomerResponse customerResponse = modelMapper.map(updatedCustomer, CustomerResponse.class);
        customerResponse.setEnvironmentDetails(applicationUtils.getCurrentEnv());
        return  customerResponse;

    }

    @PostMapping(path = "/search/{context}")
    public CustomerSearchListResponse getSearchedResult(@RequestBody SearchListRequest searchListRequest){
        log.info("Fetching Searched Data for Page --- {} with Page Size - {}" , searchListRequest.getCurrentPage() , searchListRequest.getPageSize());
        log.info("Fetching Cache for  Context - {} and Search Query  -- {}" , searchListRequest.getContext(),searchListRequest.getSearchQuery());
        log.info("Search Space --- {}",searchListRequest.getSearchSpace());
        SearchListResponse results =applicationUtils.getSearchedResult(searchListRequest);
        CustomerSearchListResponse searchListResponse = new CustomerSearchListResponse();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MMM-dd hh:mm:ss aa").create();
        searchListResponse.setSearchedDataList(results.getSearchedDataList().stream().map(item->gson.fromJson(item.toString(), Customer.class) ).collect(Collectors.toList()));
        searchListResponse.setSearchQuery(results.getSearchQuery());
        searchListResponse.setSearchSpace(results.getSearchSpace());
        searchListResponse.setContext(results.getContext());
        searchListResponse.setCurrentPage(results.getCurrentPage());
        searchListResponse.setPageSize(results.getPageSize());
        searchListResponse.setEnvironmentDetails(applicationUtils.getCurrentEnv());
        searchListResponse.setTotalElements(results.getTotalElements());
        return searchListResponse;
    }




}
