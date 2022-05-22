package com.aniket.movie.eventprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import com.aniket.movie.constant.CacheContext;
import com.aniket.movie.dto.Address;
import com.aniket.movie.dto.Customer;
import com.aniket.movie.service.AddressService;
import com.aniket.movie.service.CustomerService;
import com.aniket.movie.util.ApplicationUtils;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@EnableAsync
@Configuration
@Slf4j
public class ApplicationEventsListenrs {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private ApplicationUtils applicationUtils;

	@Async
	@EventListener
	public void customerMainDataUpdateListner(CustomerUpdateEvent event) throws Exception {
		log.info("CustomerUpdateEvent is Captured ....");
	//	Thread.sleep(5000);
		Customer customer =  (Customer) event.getPayload();
		Integer customerId = customer.getCustomerId();
		Customer findCustomerFromDB = customerService.findCustomerFromDB(customerId);
		String payload = new Gson().toJson(findCustomerFromDB);
		log.info("Payload is - {}" , payload);
		applicationUtils.putCache(String.valueOf(customerId),CacheContext.CUSTOMER, payload);
		log.info("CustomerUpdateEvent is Processed ....");
	}
	
	@Async
	@EventListener
	public void customerMainDataUpdateListner(AddressUpdateEvent event) throws Exception {
		log.info("AddressUpdateEvent is Captured .... Updating Address Part of the Contract ");
	//	Thread.sleep(5000);
		Address address =  (Address) event.getPayload();
		Integer addressId = address.getAddressId();
		log.info("Payload is - AddressId :: {}" , addressId);
	    customerService.publishCustomerUpdateForAddressChange(addressId);
		log.info("CustomerUpdateEvent is Processed .... Updating Address Part of the Contract");
	}
	

	@Async
	@EventListener
	public void addressUpdateListner(AddressUpdateEvent event) throws Exception {
		log.info("AddressUpdateEvent is Captured ....");
	//	Thread.sleep(5000);
		Address address =  (Address) event.getPayload();
		Integer addressId = address.getAddressId();
		Address findAddressFromDB = addressService.findAddressFromDB(addressId);
		String payload = new Gson().toJson(findAddressFromDB);
		log.info("Payload is - {}" , payload);
		applicationUtils.putCache(String.valueOf(addressId),CacheContext.ADDRESS, payload);
		log.info("AddressUpdateEvent is Processed ....");
		//throw new RuntimeException("All is Good ......");//Will Not work for Async
	}

}
