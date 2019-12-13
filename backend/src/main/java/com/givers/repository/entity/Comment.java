package com.givers.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document 
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
	@Id
	private String id;
	private String content;
	private String owner;
	private String causeId;
}
