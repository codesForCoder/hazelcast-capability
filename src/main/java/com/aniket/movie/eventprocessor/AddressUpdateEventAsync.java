package com.aniket.movie.eventprocessor;

import org.springframework.context.ApplicationEvent;

import lombok.Data;

@Data
public class AddressUpdateEventAsync extends ApplicationEvent {


	private Object payload;
	
	public AddressUpdateEventAsync(Object source, Object payload) {
		super(source);
		this.payload = payload;
	}
}
