package com.aniket.movie.eventprocessor;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class AddressUpdateEventInSync extends ApplicationEvent {


	private Object payload;

	public AddressUpdateEventInSync(Object source, Object payload) {
		super(source);
		this.payload = payload;
	}
}
