package com.givers.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.givers.domain.core.RecommendedCause;
import com.givers.domain.core.RecommenderService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@Service
public class RecommenderServiceImpl implements RecommenderService  {
	
	private static final int DEFAULT_COUNT = 5;
	@Value("${recommender.url}")
	private String recommenderUrl;
	@Value("${recommender.path}")
	private String recommenderPath;
	
	@Autowired
	public RecommenderServiceImpl() {
	}
	
	public Flux<RecommendedCause> recommend(String username, final int count) {
		return WebClient
				.create(recommenderUrl)
				.get()
				.uri(builder -> builder
						.path(recommenderPath+"{username}")
					    .queryParam("count", count == 0 ? DEFAULT_COUNT : count)
					    .build(username)
				)
				.accept(MediaType.APPLICATION_JSON)
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.retrieve()
				.bodyToFlux(RecommendedCause.class)
				.flatMap(recommendedCause -> { 
					recommendedCause.setId(recommendedCause.get_id().getId());
					return Flux.just(recommendedCause);
				});
	}
}
