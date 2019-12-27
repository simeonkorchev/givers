package com.givers.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.givers.repository.entity.Authority;
import com.givers.repository.entity.Role;
import com.givers.repository.entity.User;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Log4j2
@DataMongoTest
@Import({ReactiveUserDetailsServiceImpl.class, BCryptPasswordEncoder.class, UserServiceImpl.class})
public class ReactiveUserDetailsServiceImplTest {
	private final ReactiveUserDetailsServiceImpl service;
	private final UserServiceImpl userService;
	
	@Autowired
	public ReactiveUserDetailsServiceImplTest(ReactiveUserDetailsServiceImpl service, UserServiceImpl userService ) {
		this.service = service;
		this.userService = userService;
	}
	
	@Test
	public void findByUsername() {
		List<Authority> authorities = new ArrayList<>();
		authorities.add(new Authority(Role.ROLE_USER.toString()));		
		User user = this.userService
				.create("Test", "Test", "email@email.com", "user12", "pass1234", null, null, null, null, 0, authorities)
				.block();
		Mono<UserDetails> userDetails = this.service.findByUsername(user.getUsername());
		
		StepVerifier
			.create(userDetails)
			.expectNextMatches(found -> {
				List<String> auths = new ArrayList<>();
				found.getAuthorities().stream().forEach(auth -> auths.add(auth.getAuthority()));
				return found.getUsername().equals(user.getUsername()) &&
						auths.contains(Role.ROLE_USER.toString());
			})
			.verifyComplete();
	}
}
