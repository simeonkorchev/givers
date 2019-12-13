package com.givers.event;

import org.springframework.context.ApplicationEvent;

public class CommentCreatedEvent extends ApplicationEvent {

	public CommentCreatedEvent(Object source) {
		super(source);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
