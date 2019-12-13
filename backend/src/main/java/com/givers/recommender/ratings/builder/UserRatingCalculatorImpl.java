package com.givers.recommender.ratings.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.givers.repository.database.CollectorRepository;
import com.givers.repository.entity.EventType;
import com.givers.repository.entity.Log;

import reactor.core.publisher.Mono;

public class UserRatingCalculatorImpl implements UserRatingCalculator {
	private static final int W1 = 100;
	private static final int W2 = 50;
	private static final int W3 = 15;
	private final CollectorRepository repository;
//	private final Map<String, Integer> WEIGHTS = Map.of(
//		EventType.ATTEND.name(), W1,
//		EventType.CAUSE_DETAILS_VIEWED.name(), W2,
//		EventType.CAUSE_TYPE_VIEWED.name(), W3
//	);
	private final Map<String, Map<String, Double>> ratings;
	
	private double maxRating;
	
	@Autowired
	public UserRatingCalculatorImpl(CollectorRepository repository) {
		this.repository = repository;
		this.ratings = new HashMap<>();
		this.maxRating = 0;
	}
	
	@Override
	public Mono<Map<String, Rating>> calculate(String username) {
		return this.repository
			.findByUsername(username)
			.map(log -> {
				return populateEventCount(log);
			})
			.collectList()
			.map(logs -> {
				return calculateRatings(logs);
			});
	}

	private Log populateEventCount(Log log) {
		if(!this.ratings.containsKey(log.getCauseId())) {
			Map<String, Double> eventCount = new HashMap<String, Double>();
			eventCount.put(log.getEventType(), 1.0);
			this.ratings.put(log.getCauseId(), eventCount);
			return log;
		}
		Map<String, Double> eventCount = this.ratings.get(log.getCauseId());
		if(!eventCount.containsKey(log.getEventType())) {
			eventCount.put(log.getEventType(), 1.0);
			return log;
		}
		double count = eventCount.get(log.getEventType());
		eventCount.put(log.getEventType(),count+1);
		return log;
	}
	
	private Map<String, Rating> calculateRatings(List<Log> logs) {
		Map<String, Rating> ratings = new HashMap<>();
		logs.forEach(log -> {
			if(!ratings.containsKey(log.getCauseId())) {
				ratings.put(log.getCauseId(), calculateRating(log));
			}
		});
		return ratings;
	}
	private Rating calculateRating(Log log) {
		Map<String, Double> eventCount = this.ratings.get(log.getCauseId());
		double attendCount = 0.0;
		double causeDetailsViewedCount = 0.0;
		double causeTypeViewedCount = 0.0;
		
		if(eventCount.containsKey(EventType.ATTEND.name())) {
			attendCount = eventCount.get(EventType.ATTEND.name());
		}
		if(eventCount.containsKey(EventType.CAUSE_DETAILS_VIEWED.name())) {
			causeDetailsViewedCount = eventCount.get(EventType.CAUSE_DETAILS_VIEWED.name());
		}
		if(eventCount.containsKey(EventType.CAUSE_TYPE_VIEWED.name())) {
			causeTypeViewedCount = eventCount.get(EventType.CAUSE_TYPE_VIEWED.name());
		}
		double rating = W1 * attendCount  + 
						W2 * causeDetailsViewedCount +
						W3 * causeTypeViewedCount;
		this.maxRating = Math.max(this.maxRating, rating);
		return new Rating(log.getUsername(), log.getCauseId(), normalizeRating(rating), System.currentTimeMillis());	
	}

	private double normalizeRating(double rating) {
		return 10 * rating / this.maxRating;
	}

}
