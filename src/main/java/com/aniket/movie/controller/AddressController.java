package com.aniket.movie.controller;


import com.aniket.movie.constant.CacheContext;
import com.aniket.movie.constant.DataSource;
import com.aniket.movie.dto.Address;
import com.aniket.movie.eventprocessor.AddressUpdateEventAsync;
import com.aniket.movie.eventprocessor.AddressUpdateEventInSync;
import com.aniket.movie.request.AddressRequest;
import com.aniket.movie.request.SearchListRequest;
import com.aniket.movie.response.*;
import com.aniket.movie.service.AddressService;
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

import java.util.stream.Collectors;

import static com.aniket.movie.constant.RestEndpoint.ADDRESS_ENDPOINT;



@Slf4j
@RestController
@RequestMapping(ADDRESS_ENDPOINT)
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
	private ApplicationEventPublisher publisher;
    
    @Autowired
    private ApplicationUtils applicationUtils;

    @Autowired
    private ConfigCatClient client;

    @GetMapping(path = "/{source}",params = {"page" , "limit" })
    public AddressListResponse getAddresses(@RequestParam int page , @RequestParam int limit ,  @PathVariable( "source") DataSource dataSrc){
        log.info("Fetching Address for Page --- {} with Page Size - {}" , page , limit);
        AddressListResponse allAddress =addressService.findAllAddress(limit,page);
        log.info("is Data Need to be received from Database - Mandatory ? - {}",dataSrc.toString());
        log.info("Enrich Address Payload wither from Cache or DB");
        DataSource finalDataSrc = dataSrc;
        allAddress.setAddressList(allAddress.getAddressList().stream().map(addr->{
            Address address = getAddress(addr.getAddressId(), finalDataSrc);
            address.setEnvironmentDetails(null);
            return address;
        }).collect(Collectors.toList()));
        allAddress.setEnvironmentDetails(applicationUtils.getCurrentEnv());
        return allAddress;
    }

    @GetMapping(path = "/{id}/{source}" )
    public Address getAddress(@PathVariable("id") int addressId , @PathVariable( "source") DataSource dataSrc){
        log.info("Fetching Address for  id -- {}" , addressId );
        log.info("is Data Need to be retreived from Database - Mandetory ? - {}", dataSrc.toString());

        Address address;
        String cache=null;
        if(DataSource.LOCAL_CACHE.equals(dataSrc)){
            cache = applicationUtils.getCacheLocal(String.valueOf(addressId), CacheContext.ADDRESS);
        }else if(DataSource.REMOTE_CACHE.equals(dataSrc)){
            cache = applicationUtils.getCacheRemote(String.valueOf(addressId), CacheContext.ADDRESS);
        }
        if(StringUtils.hasText(cache) && !DataSource.DB.equals(dataSrc)) {
        	log.info("Raw Data - {}" , cache);
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MMM-dd hh:mm:ss aa").create();
        	address =gson.fromJson(cache,Address.class);
        }else {
        	log.info("Fetching Data from Database ....");
        	address= addressService.findAddressFromDB(addressId);
        }
        address.setEnvironmentDetails(applicationUtils.getCurrentEnv());
        return address;
    }
    @PutMapping(path = "/{id}")
    public AddressResponse updateAddress(@PathVariable("id") int addressId , @RequestBody AddressRequest addressRequest){
        log.info("Updating Data From Address ID --- {} with Payload - {}" , addressId , addressRequest);
        ModelMapper modelMapper = new ModelMapper();
        Address address =  modelMapper.map(addressRequest, Address.class);
        address.setAddressId(addressId);
        Address updateAddress =addressService.updateAddress(address);

        boolean consistency_level_is_full = client.getValue(Boolean.class, "consistency_level_is_full", false);
        if(consistency_level_is_full){
            AddressUpdateEventInSync event = new AddressUpdateEventInSync(this, updateAddress);
            publisher.publishEvent(event);
        }else{
            AddressUpdateEventAsync event = new AddressUpdateEventAsync(this, updateAddress);
            publisher.publishEvent(event);

        }


        AddressResponse addressResponse = modelMapper.map(updateAddress, AddressResponse.class);
        addressResponse.setEnvironmentDetails(applicationUtils.getCurrentEnv());
        return addressResponse;
    }
    
    @PostMapping
    public ResponseEntity<String> initCache(){
        log.info("Starting Address Payload Cache Building");
        addressService.publishAllAddresses();
        return new ResponseEntity<String>("Cache Buuilding Started At Env : --"+ applicationUtils.getCurrentEnv(),HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/search")
    public AddressSearchListResponse getSearchedResult(@RequestBody SearchListRequest searchListRequest){
        log.info("Fetching Searched Data for Page --- {} with Page Size - {}" , searchListRequest.getCurrentPage() , searchListRequest.getPageSize());
        log.info("Fetching Cache for  Context - {} and Search Query  -- {}" , searchListRequest.getContext(),searchListRequest.getSearchQuery());
        log.info("Search Space --- {}",searchListRequest.getSearchSpace());
        SearchListResponse results =applicationUtils.getSearchedResult(searchListRequest);
        AddressSearchListResponse searchListResponse = new AddressSearchListResponse();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MMM-dd hh:mm:ss aa").create();
        searchListResponse.setSearchedDataList(results.getSearchedDataList().stream().map(item->gson.fromJson(item.toString(),Address.class) ).collect(Collectors.toList()));
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
