package com.aniket.movie.eventprocessor;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
@Data
public class CustomerUpdateEventAsync extends ApplicationEvent {

	private Object payload;
	
	public CustomerUpdateEventAsync(Object source, Object payload) {
		super(source);
		this.payload = payload;
	}
}
