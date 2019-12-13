package com.givers.web;
//package com.givers.rest;
//
//import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
//import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
//import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
//import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
//import static org.springframework.web.reactive.function.server.RouterFunctions.route;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.server.RequestPredicate;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import com.givers.handlers.CommentHandler;
//
//@Configuration
//public class CommentEndpointConfiguration {
//
//	@Bean
//	RouterFunction<ServerResponse> commentRoutes(CommentHandler handler) {
//		return route(i(GET("/comments")), handler::all)
//			.andRoute(i(GET("/comments/{id}")), handler::getById)
//			.andRoute(i(DELETE("/comments/{id}")), handler::deleteById)
//			.andRoute(i(POST("/comments/")), handler::create)
//			.andRoute(i(PUT("/comments/{id}")), handler::updateById);
//	}
//	
//	private static RequestPredicate i(RequestPredicate target) {
//		return new CaseInsensitiveRequestPredicate(target);
//	}
//}
