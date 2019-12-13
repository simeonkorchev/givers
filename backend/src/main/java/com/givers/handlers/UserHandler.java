//package com.givers.handlers;
//
//import org.reactivestreams.Publisher;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import com.givers.repository.entities.User;
//import com.givers.service.UserService;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.net.URI;
//
//@Component
//public class UserHandler {
//	private final UserService service;
//
//	public UserHandler(UserService service) {
//		super();	
//		this.service = service;
//	}
//	
//	public Mono<ServerResponse> all(ServerRequest r) {
//		return defaultReadResponse(this.service.all());
//	}
//	
//	public Mono<ServerResponse> updateById(ServerRequest request) {
//		Flux<User> flux = request
//				.bodyToFlux(User.class)
//				.flatMap(u -> this.service.update(id(request), u.getEmail(), u.getUsername(),
//						u.getPassword(), u.getCauses(), u.getPhotoPath(), u.getHonor(),u.getAuthorities()));
//		return defaultWriteResponse(flux);
//	}
//	
//	public Mono<ServerResponse> deleteById(ServerRequest request) {
//		return defaultReadResponse(this.service.delete(id(request)));
//	}
//	
//	public Mono<ServerResponse> getById(ServerRequest request) {
//		return defaultReadResponse(this.service.get(id(request)));
//	}
//	
//	public Mono<ServerResponse> create(ServerRequest request) {
//		Flux<User> flux = request
//				.bodyToFlux(User.class)
//				.flatMap(u -> this.service.create(
//						u.getEmail(), u.getUsername(), u.getPassword(), u.getCauses(),
//						u.getPhotoPath(), u.getHonor(), u.getAuthorities()));
//		return defaultWriteResponse(flux);
//	}
//	
//	private static Mono<ServerResponse> defaultWriteResponse(Publisher<User> users) {
//		return Mono
//				.from(users)
//				.flatMap(r -> ServerResponse
//						.created(URI.create("/users/register/" + r.getId()))
//						.contentType(MediaType.APPLICATION_JSON_UTF8)
//						.build()
//				);
//	}
//	
//	private static Mono<ServerResponse> defaultReadResponse(Publisher<User> users) {
//		return ServerResponse
//				.ok()
//				.contentType(MediaType.APPLICATION_JSON_UTF8)
//				.body(users, User.class);
//	}
//	
//	private static String id(ServerRequest r) {
//        return r.pathVariable("id");
//    }
//}
