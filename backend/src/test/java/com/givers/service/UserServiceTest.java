package com.givers.service;

import java.util.function.Predicate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import com.givers.domain.UserServiceImpl;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.User;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Log4j2
@DataMongoTest
@Import({BCryptPasswordEncoder.class, UserServiceImpl.class})
public class UserServiceTest {
	private final UserServiceImpl service;
	private static UserRepository repository;

	@Autowired
	public UserServiceTest(UserRepository repository, UserServiceImpl service) {
		UserServiceTest.repository = repository;
    	this.service = service;
    }
	
	@AfterAll
	public static void clearDbEntries() {
		repository.deleteAll();	
	}
	
	@BeforeEach
	public void clearUsernames() {
		repository.deleteAll().block();
	}
	  
	@Test 
    public void getAll() {
		log.info("Running: " + this.getClass().getName());
		log.info("Executing getAll");
		Flux<User> saved = repository.saveAll(Flux.just(new User(null, "Test", "Test", "user@bv.bg", "user", "pass1234", null, null, null, 0, null), new User(null, "Test2", "Test2", "test2@abv.bg", "test2", "test2", null, null, null, 0, null)));
		
		Flux<User> composite = this.service.all().thenMany(saved);
		Predicate<User> match = c -> saved.any(saveItem -> saveItem.getEmail().equals(c.getEmail())).block();
		StepVerifier
			.create(composite)
			.expectNextMatches(match)
			.expectNextMatches(match)
			.verifyComplete();
	}

	@Test
	public void save() {
		log.info("Executing save");

		Mono<User> userMono = this.service.create("Test", "Test", "user", "user", "pass123", null, null, null, 0, null);
		StepVerifier
			.create(userMono)
			.expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
			.verifyComplete();
	}
	
	@Test
	public void delete() {
		log.info("Executing delete");

		String email = "test@test.com";
		Mono<User> deleted = this.service
				.create("Test", "Test", email, "user123", "pass1234", null, null, null, 0, null)
				.flatMap(created -> this.service.delete(created.getId()));
		
		StepVerifier
		.create(deleted)
		.expectNextMatches(user -> user.getEmail().equals(email))
		.verifyComplete();
	}
	
	@Test
	public void update() {
		log.info("Executing update");

		Mono<User> updated = this.service
				.create("Test", "Test", "email@email.com", "user1234", "pass1234", null, null, null, 0, null)
				.flatMap(u -> this.service.update(u.getId(),"Test", "Test", "updatedemail@email.com", "user", "pass1234", null, null, null, 0, null));
		
		StepVerifier
			.create(updated)
			.expectNextMatches(u -> u.getEmail().equals("updatedemail@email.com"))
			.verifyComplete();
	}
	
	@Test
	public void findById() {
		log.info("Executing findById");

		Mono<User> found = this.service
				.create("Test", "Test", "email@email.com", "user12", "pass1234", null, null, null, 0, null)
				.flatMap(u -> this.service.get(u.getId()));
		
		StepVerifier
			.create(found)
			.expectNextMatches(u ->  {
				return StringUtils.hasText(u.getId()) && 
						u.getFirstName().equals("Test") && 
						u.getLastName().equals("Test") &&
						u.getEmail().equals("email@email.com");
			});
	}
	
	@Test
	public void changeUserPassword() {
		log.info("Executing changeUserPassword");

		PasswordEncoder encoder = new BCryptPasswordEncoder();
		Mono<User> updated = this.service
				.create("Test", "Test", "email@email.com", "user1234", "pass1234", null, null, null, 0, null)
				.flatMap(u -> this.service.changeUserPassword(u.getUsername(), "pass1234", "updatedpass1234"));
		
		StepVerifier
			.create(updated)
			.expectNextMatches(u -> encoder.matches("updatedpass1234", u.getPassword()))
			.verifyComplete();
	}
}
