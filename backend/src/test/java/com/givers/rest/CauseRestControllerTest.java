package com.givers.rest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.givers.domain.CauseServiceImpl;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.User;
import com.givers.web.CauseRestController;
import com.givers.web.UserRestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@WebFluxTest(controllers=CauseRestController.class)
@Import({CauseServiceImpl.class})
public class CauseRestControllerTest {

	@MockBean
	private CauseRepository causeRepo;
	
	 @MockBean  
	 private UserRepository userRepo;
	
	private CauseRestController classUnderTest;

	@Autowired
	public CauseRestControllerTest(CauseRestController classUnderTest) {
		super();
		this.classUnderTest = classUnderTest;
	}
	
	@Test
    public void getAll() {
    	log.info("running " + this.getClass().getName());
//    	User user = this.userRepo.save(new User(null, "Test@abv.bg", "username12345", "FirstName", "LastName", "pass1234", null, null, null, 0, null)).block();
    	
    	Mockito
    		.when(this.causeRepo.findAll())
    		.thenReturn(Flux.just(new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null),
    				new Cause("2", "Testname2", "testowner2", null, null, null, null, null,null)));
    	
    	WebTestClient
    		.bindToController(this.classUnderTest)
    		.build()
    		.get()
    		.uri("/causes")
    		.accept(MediaType.APPLICATION_JSON_UTF8)
    		.exchange()
    		.expectStatus().isOk()
    		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
    		.expectBody()
    		.jsonPath("$.[0].id").isEqualTo("1")
    		.jsonPath("$.[0].name").isEqualTo("Testname1")
    		.jsonPath("$.[0].ownerId").isEqualTo("testowner1");
    }
}
