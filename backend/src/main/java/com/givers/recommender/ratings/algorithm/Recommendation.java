package com.givers.recommender.ratings.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation implements Comparable<Recommendation> {
	private String causeId;
	private double score;
	
	@Override
	public int compareTo(Recommendation o) {
		if(this.score > o.score) {
			return -1;
		} else if(this.score < o.score) {
			return 1;
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recommendation other = (Recommendation) obj;
		if (causeId == null) {
			if (other.causeId != null)
				return false;
		} else if (!causeId.equals(other.causeId))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((causeId == null) ? 0 : causeId.hashCode());
		return result;
	}

}
