package com.givers.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.givers.recommender.ratings.algorithm.Recommender;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.entity.Cause;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@Service
public class RecommenderService {
	private final Recommender recommender;
	private final CauseRepository causeRepository;
	
	@Autowired
	public RecommenderService(Recommender recommender, CauseRepository causeRepository) {
		this.recommender = recommender;
		this.causeRepository = causeRepository;
	}
	
	public Flux<Cause> recommend(String username) {
		return this.recommender.recommend(username)
				.flatMap(recommendation -> {
					return this.causeRepository.findById(recommendation.getCauseId());
				});
	}
}
