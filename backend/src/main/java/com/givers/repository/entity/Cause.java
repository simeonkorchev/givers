package com.givers.repository.entity;

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
	private String owner;
	private String location;
	private String description;
	private String causeType;
	private String imagePath;
	private Long time;
	private List<String> commentIds;
	private List<String> participantIds;
}
