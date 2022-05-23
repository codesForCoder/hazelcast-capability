package com.aniket.movie.eventprocessor;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class CustomerUpdateEventInSync extends ApplicationEvent {

	private Object payload;

	public CustomerUpdateEventInSync(Object source, Object payload) {
		super(source);
		this.payload = payload;
	}
}
