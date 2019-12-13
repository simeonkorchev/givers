package com.givers.recommender.ratings.builder;

import java.util.Map;
import java.util.Set;

import reactor.core.publisher.Mono;

public interface UserRatingCalculator {
	Mono<Map<String, Rating>> calculate(String username);
}
