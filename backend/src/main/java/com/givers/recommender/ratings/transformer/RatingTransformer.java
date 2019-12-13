package com.givers.recommender.ratings.transformer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.givers.recommender.ratings.builder.Rating;
import com.givers.recommender.ratings.builder.RatingCalculation;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class RatingTransformer {
	private final RatingCalculation allRatings;
	private final Map<String, Rating> userRatings;
	// An ordered collection is needed to hold cause ids.
	// In that way we can construct the two Arff files correctly, so there will be no discrepancy between the two files
	private List<String> causeIds;
	private ArrayList<Attribute> attributes;
	
 	public RatingTransformer(RatingCalculation allRatings, Map<String, Rating> userRatings) {
		this.allRatings = allRatings;
		this.userRatings = userRatings;
		this.initCauseIds();
		this.initAttributes();
	}
	
	private void initAttributes() {
		this.attributes = new ArrayList<>();
		this.causeIds.forEach(id -> {
			attributes.add(new Attribute(id));
		});
	}

	private void initCauseIds() {
		Set<String> uniqueCauseIds = new HashSet<>();
		this.causeIds = new ArrayList<>();
		this.allRatings.getAllRatings().forEach(rating -> {
			uniqueCauseIds.add(rating.getCauseId());
		});
		
		uniqueCauseIds.forEach(id -> {
			this.causeIds.add(id);
		});
	}

	//TODO this approach of gathering data is not working
	//As we aim Arff like:
	// 123 NUMERIC -> attribute for each causeId
	// 124 NUMERIC
	// 125 NUMERIC
	// @data
	//0 1.2 9 -> rating for each causeId
	//Where 123(4,5) is causeId and 0 1.2 and 9 are ratings for the corresponding causeId
	//And we need to populate rating for the given user with the ratings for all causes
	//For example if current user is rated with 10 only cause with id 123, then we need
	// 123 NUMERIC
	// 124 NUMERIC
	// 125 NUMERIC
	// @data
	// 10 0 0
	//But with the current implementation the Arff is would be:
	// 123 NUMERIC
	// @data
	// 10
	//As we iterate over user ratings list (and creating attributes and Instances based only on the user ratings list, not the whole rating list)
	//See lines 55 and 59 -> all that is now fixed
	public Instances transformUserRatingsToInstances() {
		Instances dataset = new Instances("Givers dataset",attributes, 0);
		Instance instance = new DenseInstance(this.causeIds.size());
		for(int i = 0; i < this.causeIds.size(); i++) {
			String causeId = this.causeIds.get(i);
			// if user is not rated the given cause id, then set the rating as 0.0
			// else set the rating according to the user opinion for the cause.
			if(!this.userRatings.containsKey(causeId)) {
				instance.setValue(i, 0.0);
			} else {
				instance.setValue(i, this.userRatings.get(causeId).getRating());
			}
		}
		dataset.add(instance);
		return dataset;
	}
	
	public Instances transformAllRatingsToInstances(String targetedUser) {
		Instances dataset = new Instances("Givers dataset", this.attributes, 0);
		//iterate over all ratings first, then in inner loop iterate over the all ratings
		this.allRatings.getUserCausesRatings().forEach((username, causeRatings) -> {
			if(username.equalsIgnoreCase(targetedUser)) {
				return;
			}
			Instance instance = new DenseInstance(this.causeIds.size());
			for(int i = 0; i < this.causeIds.size(); i++) {
				String causeId = this.causeIds.get(i);
				// if user is not rated the given cause id, then set the rating as 0.0
				// else set the rating according to the user opinion for the cause.
				if(!causeRatings.containsKey(causeId)) {
					instance.setValue(i, 0.0);
				} else {
					instance.setValue(i, causeRatings.get(causeId).getRating());
				}
			}
			dataset.add(instance);
		});
//		for(int i = 0; i < this.causeIds.size(); i++) {
//			String causeId = this.causeIds.get(i);
//			instance.setValue(i, this.allRatings.get(causeId).getRating());
//		}
		
		return dataset;
	}
}