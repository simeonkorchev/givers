package com.givers.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.givers.domain.core.CommentService;
import com.givers.event.CommentCreatedEvent;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.CommentRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Comment;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Log4j2
@Service
public class CommentServiceImpl implements CommentService {
	private final ApplicationEventPublisher publisher;
	private final CommentRepository commentRepository;
	private final CauseRepository causeRepository;
	private final UserRepository userRepository;
	
	public CommentServiceImpl(
			ApplicationEventPublisher publisher, 
			CommentRepository repository,
			CauseRepository causeRepository,
			UserRepository userRepository) {
		super();
		this.publisher = publisher;
		this.commentRepository = repository;
		this.causeRepository = causeRepository;
		this.userRepository = userRepository;
	}
	
	public Flux<Comment> all() {
		return this.commentRepository.findAll();
	}
	
	public Mono<Comment> get(String id) {
		return this.commentRepository.findById(id);
	}
	
	public Mono<Comment> update(String id, String content, String ownerId, String causeId) {
		return this.commentRepository
				.findById(id)
				.map(c -> new Comment(c.getId(), content, ownerId, causeId))
				.flatMap(this.commentRepository::save);
	}
	
	public Mono<Comment> delete(String id) {
		return this.commentRepository
				.findById(id)
				.flatMap(c -> this.commentRepository
						.deleteById(c.getId())
						.thenReturn(c)
				);
	}
	
	public Mono<Comment> create(String content, String username, String causeId) {
		log.info("Creating comment with content ", content, " username ", username, " and cause id: ", causeId);
		return this.commentRepository
				.save(new Comment(null, content, username, causeId))
				.doOnSuccess(c -> {
					log.info("Publishing event: for comment" + c.toString());
					this.publisher.publishEvent(new CommentCreatedEvent(c));
					this.causeRepository
						.findById(causeId)
						.switchIfEmpty(raiseIllegalState("Could not find cause with id: " + causeId))
						.publishOn(Schedulers.parallel())
						.subscribe(cause -> {
							log.info("Found cause: "+cause);
							cause.setCommentIds(appendIdToList(cause.getCommentIds(), c.getId()));
							log.info("Updated cause: "+cause);
							this.causeRepository.save(cause).subscribe().dispose();
						});
						
					this.userRepository
						.findByUsername(username.toLowerCase())
						.switchIfEmpty(raiseIllegalState("Could not find user with username: " + username))
						.publishOn(Schedulers.parallel())
						.subscribe(user -> {
							log.info("Found user: "+ user);
							user.setCommentIds(appendIdToList(user.getCommentIds(), c.getId()));
							log.info("Updated user"+ user);
							this.userRepository.save(user).subscribe().dispose();
						});
				});
	}
	
	private <T> Mono<T> raiseIllegalState(String errorMsg) {
		return Mono.error(new IllegalStateException(errorMsg));
	}

	private static List<String> appendIdToList(List<String> ids, String id) {
		if(ids == null) {
			ids = new ArrayList<>();
		}
		ids.add(id);
		return ids;
	}
}
