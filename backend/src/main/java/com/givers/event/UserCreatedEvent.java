package com.givers.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Profile;

import com.givers.repository.entity.User;

public class UserCreatedEvent extends ApplicationEvent {

	public UserCreatedEvent(User user) {
		super(user);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
