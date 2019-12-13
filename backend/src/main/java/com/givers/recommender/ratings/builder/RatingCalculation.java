package com.givers.recommender.ratings.builder;

import java.util.List;
import java.util.Map;

public class RatingCalculation {
	
	private Map<String, Map<String, Rating>> userCausesRatings;
	private List<Rating> allRatings;
	
	public RatingCalculation(Map<String, Map<String, Rating>> ratings, List<Rating> allRatings) {
		this.userCausesRatings = ratings;
		this.allRatings = allRatings;
	}

	public Map<String, Map<String, Rating>> getUserCausesRatings() {
		return userCausesRatings;
	}

	public List<Rating> getAllRatings() {
		return allRatings;
	}
}
