package com.givers.domain;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import com.givers.domain.CommentServiceImpl;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.CommentRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.Comment;
import com.givers.repository.entity.User;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.junit.jupiter.api.Assertions.assertEquals;;	

@Log4j2
@DataMongoTest
@Import(CommentServiceImpl.class)
public class CommentServiceTest {
	
	private final CommentServiceImpl service;
	private final CommentRepository repository;
	private final CauseRepository causeRepo;
	private final UserRepository userRepo;
	
	@Autowired
	public CommentServiceTest(CommentServiceImpl service, CommentRepository repository, CauseRepository causeRepo, UserRepository userRepo) {
		this.repository = repository;
		this.service = service;
		this.causeRepo = causeRepo;
		this.userRepo = userRepo;
	}
	
	@Test
	public void getAll() {
		Flux<Comment> comments = this.repository.saveAll(Flux.just(new Comment(null, "Test comment", "1", null),
				new Comment(null, "Test comment", "1", null)));
		
		Flux<Comment> composite = this.service.all().thenMany(comments);
		
		Predicate<Comment> match = comment -> comments.any(c -> c.getContent().equals(comment.getContent())).block(Duration.ofMillis(400));
		
		StepVerifier
			.create(composite)
			.expectNextMatches(match)
			.expectNextMatches(match)
			.verifyComplete();
	}
	
	@Test
	public void save() throws InterruptedException {
		User user = this.userRepo.insert(new User(null, "email@email.com", "test", "x", "y", null, null, null, null, 0, null)).block();
		Cause cause = this.causeRepo.insert(new Cause(null, "name", null, null, null, null, null, null, null)).block();

		Mono<Comment> comment = this.service.create("Test", user.getUsername(), cause.getId());
		StepVerifier
			.create(comment)
			.expectNextMatches(c ->  {
				return StringUtils.hasText(c.getId()) && c.getCauseId().equals(cause.getId());
			})
			.verifyComplete();
			
		Thread.sleep(300);
		
		Mono<Cause> updatedCause = this.causeRepo.findById(cause.getId());
		StepVerifier
			.create(updatedCause)
			.expectNextMatches(uc -> uc.getCommentIds().size() == 1)
			.verifyComplete();
		
		Thread.sleep(300);
		
		Mono<User> updatedUser = this.userRepo.findById(user.getId());
		StepVerifier
			.create(updatedUser)
			.expectNextMatches(u -> u.getCommentIds().size() == 1)
			.verifyComplete();
	}
	
	@Test
	public void delete() {
		Cause cause = this.causeRepo.insert(new Cause(null, "name", null, null, null, null, null, null, null)).block();
		Mono<Comment> deleted = this.service
				.create("Test", "1", cause.getId())
				.flatMap(d -> this.service.delete((d.getId())));
		
		StepVerifier
			.create(deleted)
			.expectNextMatches(d -> d.getContent().equals("Test") && 
									d.getOwner().equals("1") &&
									d.getCauseId().equals(cause.getId()))
			.verifyComplete();
	}
	
	@Test
	public void update() {
		Cause cause = this.causeRepo.insert(new Cause(null, "name", null, null, null, null, null, null, null)).block();
		Mono<Comment> updated = this.service
				.create("Test", "1", cause.getId())
				.flatMap(c -> this.service.update(c.getId(), "UpdateTest", "2", null));
		
		StepVerifier
			.create(updated)
			.expectNextMatches(u -> u.getContent().equals("UpdateTest") &&
									u.getOwner().equals("2"))
			.verifyComplete();
	}
	
	@Test
	public void getById() {
		Cause cause = this.causeRepo.insert(new Cause(null, "name", null, null, null, null, null, null, null)).block();
		
		Mono<Comment> found = this.service
				.create("Test", "1", cause.getId())
				.flatMap(f -> this.service.get(f.getId()));
		
		StepVerifier
			.create(found)
			.expectNextMatches(f -> f.getContent().equals("Test") &&
									f.getCauseId().equals(cause.getId()))
			.verifyComplete();
	}

}
