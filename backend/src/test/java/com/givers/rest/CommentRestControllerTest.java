package com.givers.rest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.givers.domain.CauseServiceImpl;
import com.givers.domain.CommentServiceImpl;
import com.givers.domain.UserServiceImpl;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.CommentRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.Comment;
import com.givers.web.CommentRestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@WebFluxTest(controllers=CommentRestController.class)
@Import({CommentServiceImpl.class, CauseServiceImpl.class, UserServiceImpl.class, BCryptPasswordEncoder.class})
public class CommentRestControllerTest {
	
	@MockBean
	CommentRepository repository;
	@MockBean
	CauseRepository causeRepo;
	@MockBean
	UserRepository userRepo;
	
	CommentRestController classUnderTest;
	
	@Autowired
	public CommentRestControllerTest(CommentRestController classUnderTest) {
		this.classUnderTest = classUnderTest;
	}
	
	@Test
    public void getAll() {
    	log.info("running " + this.getClass().getName());

    	Mockito
    		.when(this.repository.findAll())
    		.thenReturn(Flux.just(new Comment("1", "Test comment", "1", "3"),
    				new Comment("2", "Test comment2", "2", "3")));
    	
    	WebTestClient
    		.bindToController(this.classUnderTest)
    		.build()
    		.get()
    		.uri("/comments")
    		.accept(MediaType.APPLICATION_JSON_UTF8)
    		.exchange()
    		.expectStatus().isOk()
    		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
    		.expectBody()
    		.jsonPath("$.[0].id").isEqualTo("1")
    		.jsonPath("$.[0].content").isEqualTo("Test comment")
    		.jsonPath("$.[0].owner").isEqualTo("1")
    		.jsonPath("$.[0].causeId").isEqualTo("3");

    }

    @Test
    public void getById() {
    	Mockito
    		.when(this.repository.findById(Mockito.anyString()))
    		.thenReturn(Mono.just(new Comment("1", "Test comment", "1", "3")));
    	
    	WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.get()
		.uri("/comments/1")
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		.expectBody()
		.jsonPath("$.id").isEqualTo("1")
		.jsonPath("$.content").isEqualTo("Test comment")
		.jsonPath("$.owner").isEqualTo("1")
		.jsonPath("$.causeId").isEqualTo("3");
    }
    
}
