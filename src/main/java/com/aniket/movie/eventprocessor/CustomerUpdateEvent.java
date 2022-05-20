package com.aniket.movie.eventprocessor;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
@Data
public class CustomerUpdateEvent extends ApplicationEvent {

	private Object payload;
	
	public CustomerUpdateEvent(Object source, Object payload) {
		super(source);
		this.payload = payload;
	}
}
