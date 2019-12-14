package com.givers.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.givers.recommender.ratings.algorithm.Recommender;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.entity.Cause;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@Service
public class RecommenderService {
	
	private final CauseRepository causeRepository;
	private final WebClient client;
	@Value("${recommender.url}")
	private String recommenderUrl;
	
	@Autowired
	public RecommenderService(CauseRepository causeRepository) {
		this.causeRepository = causeRepository;
		this.client = WebClient.create(this.recommenderUrl);
	}
	
	public Flux<Cause> recommend(String username) {
		return null;
//		client
//			.get()
//			.uri("/api/v1/recommend/{username}", username)
//			.accept(MediaType.APPLICATION_JSON)
//			.exchange()
//			.flatMapMany(response -> response.bodyToFlux(elementClass))
	}
}
