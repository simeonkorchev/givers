package com.givers.event;

import org.springframework.context.ApplicationEvent;

import com.givers.repository.entity.Cause;

public class CauseCreatedEvent extends ApplicationEvent {

	public CauseCreatedEvent(Cause source) {
		super(source);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
