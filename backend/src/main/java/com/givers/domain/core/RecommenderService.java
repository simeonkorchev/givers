package com.givers.domain.core;

import reactor.core.publisher.Flux;

public interface RecommenderService {
	Flux<RecommendedCause> recommend(String username, final int count);
}
