package com.givers.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import com.givers.repository.entity.Cause;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CauseService {

	Flux<Cause> all(); 
	Mono<Cause> get(String id);
	Flux<Cause> getByOwnerId(String ownerId);
	Mono<Cause> update(
			String id, 
			String name,
			String userId, 
			String location, 
			String description, 
			String causeType, 
			Long time, 
			List<String> commentIds, 
			List<String> participantIds);
	Mono<Cause> create(
			String name, 
			String ownerId, 
			String location, 
			String description, 
			String causeType, 
			Long time, 
			List<String> commentIds, 
			List<String> participantIds); 
	Mono<Cause> delete(String id);
	//TODO think of a way to extract the following in a separate service (component)?
	Flux<Cause> getUserParticipation(String ownerId);
	Mono<Cause> updateAttendanceList(Cause cause, String username);
}
