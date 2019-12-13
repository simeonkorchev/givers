package com.givers.web;
//package com.givers.rest;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.server.RequestPredicate;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import com.givers.handlers.CauseHandler;
//
//import static org.springframework.web.reactive.function.server.RequestPredicates.*;
//import static org.springframework.web.reactive.function.server.RouterFunctions.route;
//
//@Configuration
//public class CauseEndpointConfiguration {
//
//	@Bean
//	RouterFunction<ServerResponse> causeRoutes(CauseHandler handler) {
//		return route(i(GET("/causes")), handler::all)
//				.andRoute(i(GET("/causes/{id}")), handler::getById)
//				.andRoute(i(DELETE("/causes/{id}")), handler::deleteById)
//				.andRoute(i(POST("/causes")), handler::create)
//				.andRoute(i(PUT("/causes")), handler::updateById);
//				
//	}
//	
//	private static RequestPredicate i(RequestPredicate t ) {
//		return new CaseInsensitiveRequestPredicate(t);
//	}
//}
