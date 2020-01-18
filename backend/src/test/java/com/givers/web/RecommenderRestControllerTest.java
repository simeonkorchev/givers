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

import com.givers.domain.RecommenderServiceImpl;
import com.givers.domain.core.RecommendedCause;
import com.givers.repository.entity.Cause;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@WebFluxTest(controllers=RecommenderRestController.class)
@Import({RecommenderServiceImpl.class})
public class RecommenderRestControllerTest {
	@MockBean
	private RecommenderServiceImpl service;
	
	private final RecommenderRestController classUnderTest;
	
	@Autowired
	public RecommenderRestControllerTest(RecommenderRestController r) {
		this.classUnderTest = r;
	}

	@Test
	public void getRecommendations() {
		String username = "test";
		int count = 1;
		RecommendedCause rc = new RecommendedCause("1", "test", username, username, username, username, null, null, null, null, null);
		Mockito
			.when(this.service.recommend(username, count))
			.thenReturn(Flux.just(rc));
		
		WebTestClient
		.bindToController(this.classUnderTest)
		.build()
		.get()
		.uri("/recommend/" + username + "?count=" + count)
		.accept(MediaType.APPLICATION_JSON_UTF8)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
		.expectBody()
		.jsonPath("$.[0].id").isEqualTo("1")
		.jsonPath("$.[0].name").isEqualTo(rc.getName());
	}
}
