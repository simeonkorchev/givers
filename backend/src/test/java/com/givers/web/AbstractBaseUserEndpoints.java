package com.givers.web;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log4j2
@WebFluxTest 
public abstract class AbstractBaseUserEndpoints {
    private final WebTestClient client; 
    private final MediaType JSON_UTF8 = MediaType.APPLICATION_JSON_UTF8;

    @MockBean
    private UserRepository repository;
    
    public AbstractBaseUserEndpoints(WebTestClient client) {
        this.client = client;
    }
    
    @Test
    public void getAll() {
        log.info("running  " + this.getClass().getName());
//        Mockito
//        	.when(this.repository.findAll())
//        	.thenReturn(Flux.just(new User("1", "A", null, null, null, null, null, null, 0), new User("2","B", null, null, null, null, null, null, 0)));
//        
//        this.client
//        	.get()
//        	.uri("/users")
//        	.accept(JSON_UTF8)
//        	.exchange()
//        	.expectStatus().isOk()
//            .expectHeader().contentType(JSON_UTF8)
//            .expectBody()
//            .jsonPath("$.[0].id").isEqualTo("1")
//            .jsonPath("$.[0].email").isEqualTo("A")
//            .jsonPath("$.[1].id").isEqualTo("2")
//            .jsonPath("$.[1].email").isEqualTo("B");      
    }
    
    @Test
    public void save() {
//    	User data = new User("123", UUID.randomUUID().toString() + "@email.com", null, null, null, null, null, null, 0);
//    	Mockito
//    		.when(this.repository.save(Mockito.any(User.class)))
//    		.thenReturn(Mono.just(data));
//    	
//    	this.client
//    		.post()
//    		.uri("/users")
//    		.contentType(JSON_UTF8)
//    		.body(Mono.just(data), User.class)
//    		.exchange()
//    		.expectStatus().isCreated();
    }
    
    @Test
    public void getById() {
//    	User data = new User("1", "test", null, null, null, null, null, null, 0);
//    	Mockito
//    		.when(this.repository.findById(data.getId()))
//    		.thenReturn(Mono.just(data));
//    	
//    	this.client
//    		.get()
//    		.uri("/users/" + data.getId())
//    		.accept(JSON_UTF8)
//    		.exchange()
//    		.expectStatus().isOk()
//    		.expectHeader().contentType(JSON_UTF8)
//    		.expectBody()
//    		.jsonPath("$.id").isEqualTo("1")
//    		.jsonPath("$.email").isEqualTo("test");
    }
    
    @Test
    public void delete() {
//    	User data = new User("1", "test", null, null, null, null, null, null, 0);
//    	Mockito
//    		.when(this.repository.findById(data.getId()))
//    		.thenReturn(Mono.just(data));
//    	
//    	Mockito
//    		.when(this.repository.deleteById(data.getId()))
//    		.thenReturn(Mono.empty());
//    		
//    	this.client
//    		.delete()
//    		.uri("/users/" + data.getId())
//    		.exchange()
//    		.expectStatus().isOk();
    }
    
    @Test
    public void update() {
//    	User data = new User("1", "test", null, null, null, null, null, null, 0);
//    	Mockito
//    		.when(this.repository.findById(data.getId()))
//    		.thenReturn(Mono.just(data));
//    	
//    	Mockito
//    		.when(this.repository.save(data))
//    		.thenReturn(Mono.empty());
//    	
//    	this.client
//    		.put()
//    		.uri("/users/" + data.getId())
//    		.accept(JSON_UTF8)
//    		.body(Mono.just(data), User.class)
//    		.exchange()
//    		.expectStatus().isOk();
    	
    }
    
}
