package com.aniket.movie.controller;


import com.aniket.movie.constant.CacheContext;
import com.aniket.movie.dto.Address;
import com.aniket.movie.eventprocessor.AddressUpdateEvent;
import com.aniket.movie.request.AddressRequest;
import com.aniket.movie.response.AddressResponse;
import com.aniket.movie.service.AddressService;
import com.aniket.movie.util.ApplicationUtils;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    

    @GetMapping(path = "/{id}" , params = {"noCache"})
    public Address getAddress(@PathVariable("id") int addressId , @RequestParam(defaultValue = "false" , required = false)String  noCache){
        log.info("Fetching Address for  id -- {}" , addressId );
        log.info("is Data Need to be treived from Database - Mandetory ? - {}",noCache);
        Boolean isFromDB = Boolean.valueOf(noCache);
        Address address;
        String cache = applicationUtils.getCache(String.valueOf(addressId), CacheContext.ADDRESS);
        if(StringUtils.hasText(cache) & !isFromDB) {
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
        AddressUpdateEvent event = new AddressUpdateEvent(this, updateAddress);
		publisher.publishEvent(event);
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


}
