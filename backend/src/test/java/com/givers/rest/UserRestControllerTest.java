package com.givers.rest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.givers.domain.PasswordResetTokenServiceImpl;
import com.givers.domain.UserServiceImpl;
import com.givers.domain.core.PasswordResetTokenService;
import com.givers.repository.database.PasswordResetTokenRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.User;
import com.givers.web.UserRestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@WebFluxTest(controllers=UserRestController.class)
@Import({UserServiceImpl.class, PasswordResetTokenServiceImpl.class, BCryptPasswordEncoder.class})
public class UserRestControllerTest {
    
    @MockBean  
    private UserRepository userRepo;
    
    @MockBean
    private PasswordResetTokenRepository passResetTokenRepo;
    
    private UserRestController classUnderTest;
    
    @Autowired
    public UserRestControllerTest(UserRestController controller) {
    	this.classUnderTest = controller;
    }
    
    @Test
    public void getAll() {
    	log.info("running " + this.getClass().getName());
    	Mockito
    		.when(this.userRepo.findAll())
    		.thenReturn(Flux.just(new User("1", "A", "S", "D", null, null, null, null, null, 0, null)));
    	
    	WebTestClient
    		.bindToController(this.classUnderTest)
    		.build()
    		.get()
    		.uri("/users")
    		.accept(MediaType.APPLICATION_JSON_UTF8)
    		.exchange()
    		.expectStatus().isOk()
    		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
    		.expectBody()
    		.jsonPath("$.[0].id").isEqualTo("1")
    		.jsonPath("$.[0].email").isEqualTo("A")
    		.jsonPath("$.[0].username").isEqualTo("S")
    		.jsonPath("$.[0].firstName").isEqualTo("D");
    }
    
    @Test
    public void getById() {
    	Mockito
    		.when(this.userRepo.findById(Mockito.anyString()))
    		.thenReturn(Mono.just(new User("1", "A", "S", "D", null, null, null, null, null, 0, null)));
    	
    	WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.get()
		.uri("/users/1")
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		.expectBody()
		.jsonPath("$.id").isEqualTo("1")
		.jsonPath("$.email").isEqualTo("A")
		.jsonPath("$.username").isEqualTo("S")
		.jsonPath("$.firstName").isEqualTo("D");
    }
    
	@Test
    public void create() {
    	User user = new User("1", "A", "S", "D", "F", "G", null, null, null, 0, null);
    	Mockito
    		.when(this.userRepo.save(Mockito.any(User.class)))
    		.thenReturn(Mono.just(user));

    	WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.post()
		.uri("/users")
		.body(BodyInserters.fromPublisher(Mono.just(user), User.class))
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		.expectBody().isEmpty();
    }
    
	@Test
	public void deleteById() {
    	User user = new User("1", "A", "S", "D", "F", "G", null, null, null, 0, null);
		Mockito
			.when(this.userRepo.findById("1"))
			.thenReturn(Mono.just(user));
    	Mockito
			.when(this.userRepo.deleteById("1"))
			.thenReturn(Mono.empty());

    	WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.delete()
		.uri("/users/1")
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo("1")
		.jsonPath("$.email").isEqualTo("A")
		.jsonPath("$.username").isEqualTo("S")
		.jsonPath("$.firstName").isEqualTo("D");
	}
	
	@Test
	public void updateById() {
		User user = new User("1", "A@abv.bg", "user", "FirstName", "LastName", "pass", null, null, null, 0, null);
    	Mockito
    		.when(this.userRepo.save(Mockito.any(User.class)))
    		.thenReturn(Mono.just(user));
    	Mockito
		.when(this.userRepo.findById("1"))
		.thenReturn(Mono.just(user));
    	
    	WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.put()
		.uri("/users/1")
		.body(BodyInserters.fromPublisher(Mono.just(user), User.class))
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isOk()
		.expectBody().isEmpty();
	}
	
	@Test
	public void changeUserPassword() {
		User user = new User("1", "A@abv.bg", "user", "FirstName", "LastName", new BCryptPasswordEncoder().encode("pass"), null, null, null, 0, null);
    	Mockito
    		.when(this.userRepo.save(Mockito.any(User.class)))
    		.thenReturn(Mono.just(user));
    	Mockito
		.when(this.userRepo.findByUsername("user"))
		.thenReturn(Mono.just(user));
    	
       	WebTestClient
    		.bindToController(this.classUnderTest)
    		.build()
    		.put()
    		.uri("/users/updatePassword?oldPassword=pass&newPassword=newpass")
    		.body(BodyInserters.fromPublisher(Mono.just(user), User.class))
    		.accept(MediaType.APPLICATION_JSON_UTF8)
    		.exchange()
    		.expectStatus().isOk()
    		.expectBody()
    		.jsonPath("$.id").isEqualTo("1")
    		.jsonPath("$.email").isEqualTo("A@abv.bg")
    		.jsonPath("$.username").isEqualTo("user")
    		.jsonPath("$.firstName").isEqualTo("FirstName")
       		.jsonPath("$.lastName").isEqualTo("LastName");
    		
	}
}
