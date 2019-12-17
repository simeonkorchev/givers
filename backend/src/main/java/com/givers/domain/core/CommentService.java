package com.givers.domain.core;

import com.givers.repository.entity.Comment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService {
	Flux<Comment> all();
	Mono<Comment> get(String id);
	Mono<Comment> update(String id, String content, String ownerId, String causeId);
	Mono<Comment> delete(String id);
	Mono<Comment> create(String content, String owner, String causeId);
}
