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
//import com.givers.handlers.UserHandler;
//
//@Configuration
//public class UserEndpointConfiguration {
//	@Bean
//    RouterFunction<ServerResponse> userRoutes(UserHandler handler) { 
//        return route(i(GET("/users")), handler::all) 
//            .andRoute(i(GET("/users/{id}")), handler::getById)
//            .andRoute(i(DELETE("/users/{id}")), handler::deleteById) 
//            .andRoute(i(POST("/users")), handler::create)
//            .andRoute(i(PUT("/users/{id}")), handler::updateById);
//    }
//
//    private static RequestPredicate i(RequestPredicate target) {
//        return new CaseInsensitiveRequestPredicate(target);
//    }
//}
