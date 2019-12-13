package com.givers.repository.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cause {
	@Id
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
}
