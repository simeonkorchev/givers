package com.givers.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.givers.repository.entity.Cause;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedCause {
	private String id;
	private String name;
	private String ownerId;
	private String location;
	private String description;
	private String causeType;
	private Long time;
	//TODO make this collections Set
	private List<String> commentIds;
	private List<String> participantIds;
	private Id _id;
}

