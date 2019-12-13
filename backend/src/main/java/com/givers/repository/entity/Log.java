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
public class Log {
	@Id
	String id;
	String username;
	String causeId;
	String eventType;
	String causeName;
	long created;
}
