//package com.givers.recommender.ratings.builder;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.givers.recommender.ratings.algorithm.Recommendation;
//import com.givers.repository.repositories.CollectorRepository;
//
//import reactor.core.publisher.Mono;
//
//public class ImplicitRatingCalculator {
//	
//	private final RatingCalculator calculator;
//	private final UserRatingCalculator userRatingCalculator;
//	
//	public ImplicitRatingCalculator(CollectorRepository repository) {
//		this.calculator = new RatingCalculatorImpl(repository);
//		this.userRatingCalculator = new UserRatingCalculatorImpl(repository);
//	}
//	
//	public Mono<List<Rating>> calculate(String username) {
//			List<Recommendation> rating = new ArrayList<>();
//			return Mono.just(recs)
//					.zipWith(this.calculator.calculate())
//					.zipWith(this.userRatingCalculator.calculate(username))
//					.map
//	}
//}
