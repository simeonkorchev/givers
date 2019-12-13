//package com.givers.handlers;
//
//
//import java.net.URI;
//
//import org.reactivestreams.Publisher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import com.givers.repository.entities.Comment;
//import com.givers.service.CommentService;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Component
//public class CommentHandler {
//	private final CommentService service;
//	
//	public CommentHandler(@Autowired CommentService service) {
//		this.service = service;
//	}
//	
//	public Mono<ServerResponse> all(ServerRequest r) {
//		return defaultReadResponse(this.service.all());
//	}
//	
//	public Mono<ServerResponse> getById(ServerRequest r) {
//		return defaultReadResponse(this.service.get(id(r)));
//	}
//	
//	public Mono<ServerResponse> updateById(ServerRequest r) {
//		Flux<Comment> updated = r.bodyToFlux(Comment.class)
//				.flatMap(c -> this.service.update(id(r), c.getContent(), c.getOwnerId()));
//		return defaultWriteResponse(updated);
//	}
//	
//	public Mono<ServerResponse> deleteById(ServerRequest r) {
//		return defaultWriteResponse(this.service.delete(id(r)));
//	}
//	
//	public Mono<ServerResponse> create(ServerRequest r) {
//		Flux<Comment> created = r.bodyToFlux(Comment.class)
//				.flatMap(c -> this.service.create(c.getContent(), c.getOwnerId()));
//		return defaultWriteResponse(created);
//	}
//	
//	private static Mono<ServerResponse> defaultWriteResponse(Publisher<Comment> comment) {
//		return Mono
//				.from(comment)
//				.flatMap(c -> ServerResponse
//						.created(URI.create("/comments/" + c.getId()))
//						.contentType(MediaType.APPLICATION_JSON_UTF8)
//						.build());
//	}
//	
//	private static Mono<ServerResponse> defaultReadResponse(Publisher<Comment> comment) {
//		return ServerResponse
//				.ok()
//				.contentType(MediaType.APPLICATION_JSON_UTF8)
//				.body(comment, Comment.class);
//	}
//	
//	private static String id(ServerRequest r) {
//		return r.pathVariable("id");
//	}
//}
