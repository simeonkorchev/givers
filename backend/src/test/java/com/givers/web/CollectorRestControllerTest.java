package com.givers.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.givers.domain.CollectorServiceImpl;
import com.givers.repository.database.CollectorRepository;
import com.givers.repository.entity.EventType;
import com.givers.repository.entity.Log;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@WebFluxTest(controllers=CollectorRestController.class)
@Import({CollectorServiceImpl.class})
public class CollectorRestControllerTest {

	@MockBean
	private CollectorRepository repo;
	
	private CollectorRestController classUnderTest;

	@Autowired
	public CollectorRestControllerTest(CollectorRestController classUnderTest) {
		this.classUnderTest = classUnderTest;
	}
	
	@Test
	public void collectUserBehavior() {
		Log log = new Log("1","TestUsername", "TestCauseId", EventType.ATTEND.name(), "TestCause", 123L);
		Mockito
			.when(this.repo.save(Mockito.any(Log.class)))
			.thenReturn(Mono.just(log));
		
		WebTestClient
			.bindToController(this.classUnderTest)
			.build()
			.post()
			.uri("/collect")
			.body(BodyInserters.fromPublisher(Mono.just(log), Log.class))
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
			.expectBody().isEmpty();
	    	
		
	}
	
	
}
