package com.givers.recommender.ratings.builder;

import reactor.core.publisher.Mono;

public interface RatingCalculator {
	
	Mono<RatingCalculation> calculate();

}
