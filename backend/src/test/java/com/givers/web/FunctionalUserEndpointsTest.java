package com.givers.web;
//package com.givers.rest;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
//import com.givers.handlers.UserHandler;
//import com.givers.service.UserService;
//
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//@ActiveProfiles("default")
//@Import({UserEndpointConfiguration.class, 
//	UserService.class, UserHandler.class})
//public class FunctionalUserEndpointsTest extends AbstractBaseUserEndpoints {
//
//	public FunctionalUserEndpointsTest(@Autowired WebTestClient client) {
//		super(client);
//	}
//	
//	@BeforeAll
//    static void before() {
//        log.info("running default " + UserRestController.class.getName() + " tests");
//    }
//
//}
