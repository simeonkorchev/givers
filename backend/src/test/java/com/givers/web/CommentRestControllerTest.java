package com.givers.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.givers.domain.CauseServiceImpl;
import com.givers.domain.CommentServiceImpl;
import com.givers.domain.UserServiceImpl;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.CommentRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.Comment;
import com.givers.repository.entity.User;
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
    
	@Test
	public void deleteById() {
		Mockito
		.when(this.repository.findById(Mockito.anyString()))
		.thenReturn(Mono.just(new Comment("1", "Test comment", "1", "3")));
	
    	Mockito
			.when(this.repository.deleteById("1"))
			.thenReturn(Mono.empty());
    	
    	WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.delete()
		.uri("/comments/1")
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo("1")
		.jsonPath("$.content").isEqualTo("Test comment")
		.jsonPath("$.owner").isEqualTo("1")
		.jsonPath("$.causeId").isEqualTo("3");
	}
	
	@Test
	public void updateById() {
		Comment comment = new Comment("1", "Test comment", "1", "3");
    	Mockito
    		.when(this.repository.save(Mockito.any(Comment.class)))
    		.thenReturn(Mono.just(comment));
    	Mockito
		.when(this.repository.findById("1"))
		.thenReturn(Mono.just(comment));

    	WebTestClient
			.bindToController(this.classUnderTest)
			.build()
			.put()
			.uri("/comments/1")
			.body(BodyInserters.fromPublisher(Mono.just(comment), Comment.class))
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isOk();
	}
	
	@Test
    public void create() {
		Comment comment = new Comment("1", "Test comment", "1", "3");
		Cause cause = new Cause("1", "Testname1", "testowner1", null, null, null, null, null,null);
		User user = new User("1", "A", "S", "D", "F", "G", null, null, null, 0, null);
		
		Mockito
		.when(this.repository.save(Mockito.any(Comment.class)))
		.thenReturn(Mono.just(comment));

		Mockito
			.when(this.causeRepo.save(Mockito.any(Cause.class)))
			.thenReturn(Mono.just(cause));
    	Mockito
    		.when(this.causeRepo.findById(Mockito.anyString()))
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
			.uri("/comments")
			.body(BodyInserters.fromPublisher(Mono.just(comment), Comment.class))
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
			.expectBody().isEmpty();
    }
	
}
