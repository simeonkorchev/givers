package com.givers.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.givers.domain.CauseServiceImpl;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.User;
import com.givers.web.CauseRestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    	Mockito
    		.when(this.causeRepo.findAll())
    		.thenReturn(Flux.just(new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null, null),
    				new Cause("2", "Testname2", "testowner2", null, null, null, null, null,null, null)));
    	
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
    		.jsonPath("$.[0].owner").isEqualTo("testowner1");
    }
	
    @Test
    public void getById() {
    	Mockito
    		.when(this.causeRepo.findById(Mockito.anyString()))
    		.thenReturn(Mono.just(new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null, null)));
    	
    	WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.get()
		.uri("/causes/1")
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		.expectBody()
		.jsonPath("$.id").isEqualTo("1")
		.jsonPath("$.name").isEqualTo("Testname1")
		.jsonPath("$.owner").isEqualTo("testowner1");
    }
    
    @Test
    public void getOwnCauses() {
    	Mockito
    		.when(this.causeRepo.findByOwner(Mockito.anyString()))
    		.thenReturn(Flux.just(new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null, null)));
    	
    	WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.get()
		.uri("/causes/own/1")
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		.expectBody()
		.jsonPath("$.[0].id").isEqualTo("1")
		.jsonPath("$.[0].name").isEqualTo("Testname1")
		.jsonPath("$.[0].owner").isEqualTo("testowner1");
    }
    
    @Test
    public void getUserParticipation() {
    	List<String> userIds = new ArrayList<>();
    	userIds.add("100");
    	
    	Mockito
			.when(this.userRepo.findByUsername("testowner1"))
			.thenReturn(Mono.just(new User("1", "A", "S", "D", null, null, new ArrayList<>(), null, null, null, 0, null)));
    	Mockito
			.when(this.causeRepo.findAllById(Mockito.anyCollection()))
			.thenReturn(Flux.just(new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null, null),
				new Cause("2", "Testname2", "testowner2", null, null, null, null, null,null, null)));
	
    	WebTestClient
			.bindToController(this.classUnderTest)
			.build()
			.get()
			.uri("/causes/attend/testowner1")
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
			.expectBody()
			.jsonPath("$.[0].id").isEqualTo("1")
			.jsonPath("$.[0].name").isEqualTo("Testname1")
			.jsonPath("$.[0].owner").isEqualTo("testowner1");
    }
    
	@Test
	public void deleteById() {
    	Mockito
		.when(this.causeRepo.findById(Mockito.anyString()))
		.thenReturn(Mono.just(new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null, null)));

    	Mockito
			.when(this.causeRepo.deleteById("1"))
			.thenReturn(Mono.empty());
    	
    	WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.delete()
		.uri("/causes/1")
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo("1")
		.jsonPath("$.name").isEqualTo("Testname1")
		.jsonPath("$.owner").isEqualTo("testowner1");
	}
	
	@Test
	public void updateById() {
		Cause cause = new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null, null);
    	Mockito
    		.when(this.causeRepo.save(Mockito.any(Cause.class)))
    		.thenReturn(Mono.just(cause));
    	Mockito
		.when(this.causeRepo.findById("1"))
		.thenReturn(Mono.just(cause));

    	WebTestClient
			.bindToController(this.classUnderTest)
			.build()
			.put()
			.uri("/causes/1")
			.body(BodyInserters.fromPublisher(Mono.just(cause), Cause.class))
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isOk()
			.expectBody().isEmpty();
	}
	
	@Test
    public void create() {
		Cause cause = new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null, null);
		User user = new User("1", "A", "S", "D", "F", "G", null, null, null, null, 0, null);
		
		Mockito
			.when(this.causeRepo.save(Mockito.any(Cause.class)))
			.thenReturn(Mono.just(cause));
    	
    	Mockito
    		.when(this.userRepo.save(Mockito.any(User.class)))
    		.thenReturn(Mono.just(user));
    	Mockito
		.when(this.userRepo.findByUsername(Mockito.anyString()))
		.thenReturn(Mono.just(user));

    	WebTestClient
			.bindToController(this.classUnderTest)
			.build()
			.post()
			.uri("/causes")
			.body(BodyInserters.fromPublisher(Mono.just(cause), Cause.class))
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
			.expectBody()
			.jsonPath("$.id").isEqualTo("1")
			.jsonPath("$.name").isEqualTo("Testname1")
			.jsonPath("$.owner").isEqualTo("testowner1");
    }
	
	@Test
    public void attendToCause() {
		Cause cause = new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null, null);
		User user = new User("1", "A", "username", "D", "F", "G", null, null, null, null, 0, null);
		
		Mockito
			.when(this.causeRepo.save(Mockito.any(Cause.class)))
			.thenReturn(Mono.just(cause));
    	
    	Mockito
    		.when(this.userRepo.save(Mockito.any(User.class)))
    		.thenReturn(Mono.just(user));
    	Mockito
		.when(this.userRepo.findByUsername(user.getUsername()))
		.thenReturn(Mono.just(user));

    	WebTestClient
			.bindToController(this.classUnderTest)
			.build()
			.put()
			.uri("/causes/attend/" + user.getUsername())
			.body(BodyInserters.fromPublisher(Mono.just(cause), Cause.class))
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
			.expectBody().isEmpty();
    }
    
}
