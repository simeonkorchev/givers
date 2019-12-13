package com.givers.recommender.ratings.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.givers.repository.database.CollectorRepository;
import com.givers.repository.entity.EventType;
import com.givers.repository.entity.Log;

import reactor.core.publisher.Mono;

public class RatingCalculatorImpl implements RatingCalculator {

	private static final int W1 = 100;
	private static final int W2 = 50;
	private static final int W3 = 15;
	private final CollectorRepository repository;
	private final Map<String, Integer> WEIGHTS = Map.of(
		EventType.ATTEND.name(), W1,
		EventType.CAUSE_DETAILS_VIEWED.name(), W2,
		EventType.CAUSE_TYPE_VIEWED.name(), W3
	);
	private final Map<String, Map<String, Double>> ratings;
	
	private final Map<String, Map<String, Map<String, Double>>> allUserCauseRatings;
	
	private double maxRating;
	
	@Autowired
	public RatingCalculatorImpl(CollectorRepository repository) {
		this.repository = repository;
		this.ratings = new HashMap<>();
		this.allUserCauseRatings = new HashMap<>();
		this.maxRating = 0;
	}
	
	@Override
	public Mono<RatingCalculation> calculate() {
		return this.repository
			.findAll()
			.map(log -> {
				return populateUserCauseEventCount(log);
			})
			.collectList()
			.map(logs -> {
				return calculateRatings(logs);
			});
	}
	
	private Log populateUserCauseEventCount(Log log) {
		if(!this.allUserCauseRatings.containsKey(log.getUsername())) {
			Map<String, Map<String, Double>> currUserRatings = new HashMap<>();
			Map<String, Double> eventCount =  new HashMap<>();
			eventCount.put(log.getEventType(), 1.0);
			currUserRatings.put(log.getCauseId(), eventCount);
			this.allUserCauseRatings.put(log.getUsername(), currUserRatings);
			return log;
		}
		
		Map<String, Map<String, Double>> currUserRatings = this.allUserCauseRatings.get(log.getUsername());
		if(!currUserRatings.containsKey(log.getCauseId())) {
			//the cause ratings are not populated yet
			Map<String, Double> eventCount = new HashMap<String, Double>();
			eventCount.put(log.getEventType(), 1.0);
			currUserRatings.put(log.getCauseId(), eventCount);
			this.allUserCauseRatings.put(log.getUsername(), currUserRatings);
			return log;
		}
		
		Map<String, Double> eventCount = currUserRatings.get(log.getCauseId());
		//TODO this might need to be fixed, as we are not adding the updated values to currUserRatings and allUserRatings
		if(!eventCount.containsKey(log.getEventType())) {
			eventCount.put(log.getEventType(), 1.0);
			return log;
		}
		double count = eventCount.get(log.getEventType());
		eventCount.put(log.getEventType(),count+1);
		return log;
	}
	
	private RatingCalculation calculateRatings(List<Log> logs) {
		List<Rating> allRatings = new ArrayList<>();
		Map<String, Map<String, Rating>> ratings = new HashMap<>();
		logs.forEach(log -> {
			if(!ratings.containsKey(log.getUsername())) {
				Map<String, Rating> causeRating = new HashMap<>();
				causeRating.put(log.getCauseId(), calculateRating(log));
				ratings.put(log.getUsername(), causeRating);
			} else {
				Map<String, Rating> causeRating = ratings.get(log.getUsername());
				Rating r = calculateRating(log);
				allRatings.add(r);
				causeRating.put(log.getCauseId(), r);
				ratings.put(log.getUsername(), causeRating);
			}
		});
		return new RatingCalculation(ratings, allRatings);
	}

	private Rating calculateRating(Log log) {
		Map<String, Map<String, Double>> userRating = this.allUserCauseRatings.get(log.getUsername());
		Map<String, Double> eventCount = userRating.get(log.getCauseId());
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
