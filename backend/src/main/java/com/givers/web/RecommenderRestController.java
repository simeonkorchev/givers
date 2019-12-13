package com.givers.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.givers.domain.RecommenderService;
import com.givers.repository.entity.Cause;

import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/recommend", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecommenderRestController {
	
	public RecommenderService recommenderService;
	
	public RecommenderRestController(RecommenderService recommenderService) {
		this.recommenderService = recommenderService;
	}
	
	@GetMapping("{username}")
	Flux<Cause> getRecommendations(@PathVariable("username") String username) {
		return this.recommenderService.recommend(username);
	}
	
}
