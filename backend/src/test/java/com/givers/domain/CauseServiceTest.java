package com.givers.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import com.givers.domain.CauseServiceImpl;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import(CauseServiceImpl.class)
public class CauseServiceTest {
	private final CauseServiceImpl service;
	private static CauseRepository repository;
	private final UserRepository userRepo;
	
	@Autowired
	public CauseServiceTest(CauseServiceImpl service, CauseRepository repository, UserRepository userRepo) {
		this.service = service;
		CauseServiceTest.repository = repository;
		this.userRepo = userRepo;
	}
	
	@AfterAll
	public static void clearDbEntries() {
		repository.deleteAll();	
	}
	
	@BeforeEach
	public void clearUsernames() {
		this.userRepo.deleteAll().block();
	}
	
	@Test
    public void getAll() {
		Flux<Cause> saved = repository.saveAll(Flux.just(new Cause(null, "test1", null, null, null, null, 1123L, null, null),
													new Cause(null, "test2", null, null, null, null, 1123L, null, null)));
		
		Flux<Cause> composite = service.all().thenMany(saved);
		
		Predicate<Cause> match = c -> saved.any(saveItem -> saveItem.getName().equals(c.getName())).block();
		
		StepVerifier
			.create(composite)
			.expectNextMatches(match)
			.expectNextMatches(match)
			.verifyComplete();
	}
	
	@Test
	public void save() {
		Mono<Cause> causeMono = this.service.create("test", "testowner", "testLocation", "testDescription", null, 123L, null, null);
		StepVerifier
			.create(causeMono)
			.expectNextMatches(s -> StringUtils.hasText(s.getId()))
			.verifyComplete();
			
	}
	
	@Test
	public void delete() {
		Mono<Cause> deleted = this.service
				.create("testName", "testowner","testLocation", "testDescription", null, 123L, null, null)
				.flatMap(saved -> this.service.delete(saved.getId()));
		StepVerifier
			.create(deleted)
			.expectNextMatches(d -> d.getName().equalsIgnoreCase("testName"))
			.verifyComplete();
	}
	
	@Test
	public void update() {
		Mono<Cause> updated = this.service
				.create("test", "test", "testlocation", "testdescription",null, 123L, null, null)
				.flatMap(u -> this.service.update(u.getId(), "updatedCaseName", "updatedTest", "updatedtestlocation", "updatedtestdescription", null, 123L, null, null));
		StepVerifier
			.create(updated)
			.expectNextMatches(u -> u.getOwnerId().equalsIgnoreCase("updatedTest"))
			.verifyComplete();
	}
	
	@Test
	public void getById() {
		User user = this.userRepo.save(new User(null, "Test@abv.bg", "username", "FirstName", "LastName", "pass1234", null, null, null, 0, null)).block();
		String random = UUID.randomUUID().toString();
		Mono<Cause> deleted = this.service
				.create(random, user.getId(), null, null, null, null, null, null)
				.flatMap(saved -> this.service.get(saved.getId()));
		
		StepVerifier
			.create(deleted)
			.expectNextMatches(c -> StringUtils.hasText(c.getId()) && c.getName().equalsIgnoreCase(random))
			.verifyComplete();
	}
	
	@Test
	public void attendToCause() throws InterruptedException {
		User user = this.userRepo.save(new User(null, "Test@abv.bg", "username", "FirstName", "LastName", "pass1234", null, null, null, 0, null)).block();
		Cause createdCause = this.service
				.create("TestName", user.getId(), null, null, null, null, null, null)
				.flatMap(c -> this.service.attendToCause(c, user.getUsername())).block();
		
		Mono<Cause> attendedCause = this.service.get(createdCause.getId());
		StepVerifier
			.create(attendedCause)
			.expectNextMatches(c -> c.getParticipantIds().get(0).equals(user.getId()))
			.verifyComplete();
	}
	
	@Test
	public void getUserParticipation() throws InterruptedException {
		User user = this.userRepo.save(new User(null, "Test@abv.bg", "username12345", "FirstName", "LastName", "pass1234", null, null, null, 0, null)).block();
		List<String> ids = new ArrayList<>();
		ids.add(user.getId());

		CauseServiceTest.repository.saveAll(Flux.just(new Cause(null, "Testname1", "testowner1", null, null, null, null, null,ids),
				new Cause(null, "Testname2", "testowner2", null, null, null, null, null,null))).blockLast();
		
		Thread.sleep(300);
		
		Flux<Cause> userParticipation = this.service.getUserParticipation(user.getId());
		
		StepVerifier
			.create(userParticipation)
			.expectNextMatches(c -> c.getParticipantIds().get(0).equals(user.getId()))
			.verifyComplete();
	}
}
