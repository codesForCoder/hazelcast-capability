package com.aniket.movie.eventprocessor;

import org.springframework.context.ApplicationEvent;

import lombok.Data;

@Data
public class AddressUpdateEvent extends ApplicationEvent {


	private Object payload;
	
	public AddressUpdateEvent(Object source, Object payload) {
		super(source);
		this.payload = payload;
	}
}
