package com.givers.service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import com.givers.domain.CauseServiceImpl;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.entity.Cause;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import(CauseServiceImpl.class)
public class CauseServiceTest {
	private final CauseServiceImpl service;
	private static CauseRepository repository;
	
	public CauseServiceTest(CauseServiceImpl service, CauseRepository repository) {
		this.service = service;
		CauseServiceTest.repository = repository;
	}
	
	@AfterAll
	public static void clearDbEntries() {
		repository.deleteAll();	
	}
	@Test
    public void getAll() {
		Flux<Cause> saved = repository.saveAll(Flux.just(new Cause(null, "test1", null, null, null, null, 1123L, null, null),
													new Cause(null, "test2", null, null, null, null, 1123L, null, null)));
		
		Flux<Cause> composite = service.all().thenMany(saved);
		
		Predicate<Cause> match = c -> saved.any(saveItem -> saveItem.equals(c)).block(Duration.ofMillis(300));
		
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
		String random = UUID.randomUUID().toString();
		Mono<Cause> deleted = this.service
				.create(random, null, null, null, null, null, null, null)
				.flatMap(saved -> this.service.get(saved.getId()));
		
		StepVerifier
			.create(deleted)
			.expectNextMatches(c -> StringUtils.hasText(c.getId()) && c.getName().equalsIgnoreCase(random))
			.verifyComplete();
				
	}
}
