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

@Log4j2
@Service
public class CommentServiceImpl implements CommentService {
	private final ApplicationEventPublisher publisher;
	private final CommentRepository repository;
	private final CauseRepository causeRepository;
	private final UserRepository userRepository;
	
	public CommentServiceImpl(
			ApplicationEventPublisher publisher, 
			CommentRepository repository,
			CauseRepository causeRepository,
			UserRepository userRepository) {
		super();
		this.publisher = publisher;
		this.repository = repository;
		this.causeRepository = causeRepository;
		this.userRepository = userRepository;
	}
	
	public Flux<Comment> all() {
		return this.repository.findAll();
	}
	
	public Mono<Comment> get(String id) {
		return this.repository.findById(id);
	}
	
	public Mono<Comment> update(String id, String content, String ownerId, String causeId) {
		return this.repository
				.findById(id)
				.map(c -> new Comment(c.getId(), content, ownerId, causeId))
				.flatMap(this.repository::save);
	}
	
	public Mono<Comment> delete(String id) {
		return this.repository
				.findById(id)
				.flatMap(c -> this.repository
						.deleteById(c.getId())
						.thenReturn(c)
				);
	}
	
	public Mono<Comment> create(String content, String owner, String causeId) {
		log.info("Creating comment with content ", content, " username ", owner, " and cause id: ", causeId);
		return this.repository
				.save(new Comment(null, content, owner, causeId))
				.doOnSuccess(c -> {
					log.info(c);
					this.publisher.publishEvent(new CommentCreatedEvent(c));
					this.causeRepository
						.findById(causeId)
						.switchIfEmpty(raiseIllegalState("Could not find cause with id: " + causeId))
						.subscribe(cause -> {
							log.info("Found cause: "+cause);
							cause.setCommentIds(appendIdToList(cause.getCommentIds(), c.getId()));
							log.info("Updated cause: "+cause);
							this.causeRepository.save(cause).subscribe().dispose();
						});
						
					this.userRepository
						.findByUsername(owner)
						.switchIfEmpty(raiseIllegalState("Could not find user with username: " + owner))
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
