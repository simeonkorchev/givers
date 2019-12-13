package com.givers.service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import com.givers.domain.CommentService;
import com.givers.repository.database.CommentRepository;
import com.givers.repository.entity.Comment;
import com.givers.repository.entity.User;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Log4j2
@DataMongoTest
@Import(CommentService.class)
public class CommentServiceTest {
	
	private final CommentService service;
	private final CommentRepository repository;
	
	public CommentServiceTest(@Autowired CommentService service,  @Autowired CommentRepository repository) {
		this.repository = repository;
		this.service = service;
	}
	
	@Test
	public void getAll() {
		Flux<Comment> comments = this.repository.saveAll(Flux.just(new Comment(null, "Test comment", "1", null),
				new Comment(null, "Test comment", "1", null)));
		
		Flux<Comment> composite = this.service.all().thenMany(comments);
		
		Predicate<Comment> match = comment -> comments.any(c -> c.equals(comment)).block(Duration.ofMillis(400));
		
		StepVerifier
			.create(composite)
			.expectNextMatches(match)
			.expectNextMatches(match)
			.verifyComplete();
	}
	
	@Test
	public void save() {
		Mono<Comment> comment = this.service.create("Test", "1", null);
		StepVerifier
			.create(comment)
			.expectNextMatches(c -> StringUtils.hasText(c.getId()) &&
									c.getOwner().equalsIgnoreCase("1") &&
									c.getContent().equalsIgnoreCase("Test"))
			.verifyComplete();
	}
	
	@Test
	public void delete() {
		Mono<Comment> deleted = this.service
				.create("Test", "1", null)
				.flatMap(d -> this.service.delete((d.getId())));
		
		StepVerifier
			.create(deleted)
			.expectNextMatches(d -> d.getContent().equals("Test") && 
									d.getOwner().equals("1"))
			.verifyComplete();
	}
	
	@Test
	public void update() {
		Mono<Comment> updated = this.service
				.create("Test", "1", null)
				.flatMap(c -> this.service.update(c.getId(), "UpdateTest", "2", null));
		
		StepVerifier
			.create(updated)
			.expectNextMatches(u -> u.getContent().equals("UpdateTest") &&
									u.getOwner().equals("2"))
			.verifyComplete();
	}
	
	@Test
	public void getById() {
		String random = UUID.randomUUID().toString();
		
		Mono<Comment> found = this.service
				.create("Test", "1", random)
				.flatMap(f -> this.service.get(f.getId()));
		
		StepVerifier
			.create(found)
			.expectNextMatches(f -> f.getContent().equals(random))
			.verifyComplete();
	}

}
