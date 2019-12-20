package com.givers.web;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.givers.domain.RecommenderServiceImpl;
import com.givers.domain.core.RecommendedCause;
import com.givers.domain.core.RecommenderService;

import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/recommend")
public class RecommenderRestController {
	public RecommenderService recommenderService;
	
	public RecommenderRestController(RecommenderService recommenderService) {
		this.recommenderService = recommenderService;
	}
	
	@GetMapping("{username}")
	Flux<RecommendedCause> getRecommendations(@PathVariable("username") String username, @RequestParam("count") int count) {
		return this.recommenderService.recommend(username, count);
	}
	
}
