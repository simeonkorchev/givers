package com.givers.recommender.ratings.builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
	private String username;
	private String causeId;
	private double rating;
	private long unixTimestamp;
}
