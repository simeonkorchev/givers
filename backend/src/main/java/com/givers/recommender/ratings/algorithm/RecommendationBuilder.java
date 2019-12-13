package com.givers.recommender.ratings.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.givers.recommender.ratings.builder.Rating;
import com.givers.recommender.ratings.builder.RatingCalculation;
import com.givers.recommender.ratings.transformer.RatingTransformer;

import reactor.core.publisher.Flux;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;

public class RecommendationBuilder {
	private RatingCalculation allRatings;
	private Map<String, Rating> userRatings;
	private String username;
	
	public RecommendationBuilder(String username) {
		this.username = username;
	}
	public static RecommendationBuilder withAllRatings(RecommendationBuilder rb, RatingCalculation ratings) {
		rb.allRatings = ratings;
		return rb;
	}
	
	public static RecommendationBuilder withUserRatings(RecommendationBuilder rb, Map<String, Rating> ratings) {
		rb.userRatings = ratings;
		return rb;
	}

	public static Flux<Recommendation> build(RecommendationBuilder rb) {
		return rb.buildInternal();
	}
	
	private Flux<Recommendation> buildInternal() {
		//initialize datasets
		RatingTransformer transformer = new RatingTransformer(this.allRatings, this.userRatings);
		Instances dataset = transformer.transformAllRatingsToInstances(this.username);
		Instances userDataSet = transformer.transformUserRatingsToInstances();
		Instance userData = userDataSet.firstInstance();
		//initialize Nearest Neighbor search algorithm
		LinearNNSearch kNN = new LinearNNSearch(dataset);
		Instances neighbors = null;
	    double[] distances = null;
	    try {
	    	//get neighbors for userData
	        neighbors = kNN.kNearestNeighbours(userData, 5);
	        //get distances
	        distances = kNN.getDistances();
	    } catch (Exception e) {
	    	return Flux.error(e);
	    }

	    //init similarities
	    double[] similarities = new double[distances.length];
	    for (int i = 0; i < distances.length; i++) {
	    	//similarty is inversely proportional of the distance between the neighbors, so
	    	//the bigger the distance the smaller the similarity is
	        similarities[i] = 1.0 / distances[i];
	    }

//			    Enumeration nInstances = neighbors.enumerateInstances();
	    
	    //init the map with causeId as key and list of ratings (double)
	    Map<String, List<Double>> recommendations = new HashMap<String, List<Double>>();
	    
	    //iterate over the neighbors
	    for(int i = 0; i < neighbors.numInstances(); i++) {
	    	Instance currNeighbor = neighbors.get(i);
	    	//iterate over each cause
	    	for(int j = 0; j < currNeighbor.numAttributes(); j++) {
	    		if(userData.value(j) <= 0) {
	    			//the cause is not ranked (visited, attended, etc) by the user
	    			String attrName = userData.attribute(j).name();
	    			List<Double> lst = new ArrayList<>();
	    			if(recommendations.containsKey(attrName)) {
	    				lst = recommendations.get(attrName);
	    			}
	    			//append the neighbor's rating to the list of ratings for the current cause
	    			lst.add((double)currNeighbor.value(j));
	    			recommendations.put(attrName, lst);
	    		}
	    	}
	    }
	    
	    //initialize the Recommendation list
	    List<Recommendation> finalRanks = new ArrayList<>();
	    //obtain the iterator for the recommendations
	    Iterator<String> it = recommendations.keySet().iterator();
	    //loop over the recommendations
	    while(it.hasNext()) {
	    	String attrName = it.next();
	    	double impact = 0;
	    	double weightedSum = 0;
	    	List<Double> ranks = recommendations.get(attrName);
	    	//loop over the ranks for each recommendation
	    	for(int i = 0; i < ranks.size(); i++) {
	    		double val = ranks.get(i); //get neighbor's ranking
	    		impact += similarities[i]; //accumulate similarty weight
	    		weightedSum += similarities[i] * val; //accumulate recommendation score
	    	}
	    	//add the recomendation to the final list, by normalizing the scores
	    	finalRanks.add(new Recommendation(attrName, weightedSum / impact));
	    }
	    //sort in the decreasing order
	    Collections.sort(finalRanks);
		return Flux.fromIterable(finalRanks);
	}
	
}
