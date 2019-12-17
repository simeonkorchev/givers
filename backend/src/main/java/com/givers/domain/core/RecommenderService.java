package com.givers.domain.core;

import com.givers.domain.RecommendedCause;

import reactor.core.publisher.Flux;

public interface RecommenderService {
	Flux<RecommendedCause> recommend(String username, final int count);
}
