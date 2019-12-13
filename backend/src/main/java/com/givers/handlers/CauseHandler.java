//package com.givers.handlers;
//
//import java.net.URI;
//
//import org.reactivestreams.Publisher;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import com.givers.repository.entities.Cause;
//import com.givers.service.CauseService;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Component
//public class CauseHandler {
//
//	private CauseService service;
//
//	public CauseHandler(CauseService service) {
//		super();
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
//		Flux<Cause> cause = r.bodyToFlux(Cause.class)
//				.flatMap(c -> this.service.update(id(r), c.getName(), c.getOwnerId(), c.getLocation(), c.getDescription(), c.getTime(), c.getCommentIds(), c.getParticipantIds()));
//		return defaultWriteResponse(cause);
//	}
//
//	public Mono<ServerResponse> deleteById(ServerRequest r) {
//		return defaultWriteResponse(this.service.delete(id(r)));
//	}
//	
//	public Mono<ServerResponse> create(ServerRequest r) {
//		Flux<Cause> created = r.bodyToFlux(Cause.class)
//				//String name, User owner, String location, String description, Date time, List<Comment> comments, List<User> participants) {
//				.flatMap(c -> this.service.create(c.getName(), c.getOwnerId(), c.getLocation(), c.getDescription(), c.getTime(), c.getCommentIds(),
//						c.getParticipantIds()));
//		return defaultWriteResponse(created);
//	}
//	
//	private static Mono<ServerResponse> defaultWriteResponse(Publisher<Cause> causes) {
//		return Mono
//				.from(causes)
//				.flatMap(r -> ServerResponse
//						.created(URI.create("/causes/" + r.getId()))
//						.contentType(MediaType.APPLICATION_JSON_UTF8)
//						.build());
//	}
//
//	private static Mono<ServerResponse> defaultReadResponse(Publisher<Cause> causes) {
//		return ServerResponse
//				.ok()
//				.contentType(MediaType.APPLICATION_JSON_UTF8)
//				.body(causes, Cause.class);
//	}
//	
//	private static String id(ServerRequest r) {
//		return r.pathVariable("id");
//	}
//}
