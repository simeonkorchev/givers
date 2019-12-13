package com.givers.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import com.givers.domain.UserService;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.function.Predicate;

@Log4j2
@DataMongoTest
@Import(UserService.class)
public class UserServiceTest {
	private final UserService service;
	private final UserRepository repository;

	public UserServiceTest(@Autowired UserService service,
    		@Autowired UserRepository repository) {
    	this.service = service;
    	this.repository = repository;
    }
	
	@Test 
    public void getAll() {
//		Flux<User> saved = this.repository.saveAll(Flux.just(new User(null, "Simeon", "Simeon", null, null, null, null, null, 0), new User(null, "Simona", "Simona", null, null, null, null, null, 0)));
//		
//		Flux<User> composite = this.service.all().thenMany(saved);
//		
//		Predicate<User> match = user -> saved.any(saveItem -> saveItem.equals(user)).block();
//		StepVerifier
//			.create(composite)
//			.expectNextMatches(match)
//			.expectNextMatches(match)
//			.verifyComplete();
	}

	@Test
	public void save() {
//		Mono<User> userMono = this.service.create("email@email.com", "test0", null, null, false, null, null, 0);
//		StepVerifier
//			.create(userMono)
//			.expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
//			.verifyComplete();
	}
	
	@Test
	public void delete() {
//		String email = "test@test.com";
//		Mono<User> deleted = this.service
//				.create(email, "test1", "test2", null, false, null, email, 0)
//				.flatMap(created -> this.service.delete(created.getId()));
//		
//		StepVerifier
//		.create(deleted)
//		.expectNextMatches(user -> user.getEmail().equals(email))
//		.verifyComplete();
	}
	
	@Test
	public void update() {
//		Mono<User> updated = this.service
//				.create("test1", "test123", null, null, false, null, null, 0)
//				.flatMap(u -> this.service.update(u.getId(), "update", null, null, null, false, null, null, 0));
//		
//		StepVerifier
//			.create(updated)
//			.expectNextMatches(u -> u.getEmail().equals("update"))
//			.verifyComplete();
	}
	
	@Test
	public void findById() {
//        String uuid = UUID.randomUUID().toString();
//        
//		Mono<User> found = this.service
//				.create(uuid, "test", "test", null, false, null, uuid, 0)
//				.flatMap(u -> this.service.get(u.getId()));
//		
//		StepVerifier
//			.create(found)
//			.expectNextMatches(u -> StringUtils.hasText(u.getEmail()) && uuid.equalsIgnoreCase(u.getEmail()));
	}
}
