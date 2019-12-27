package com.givers.domain.core;

import java.util.List;

import com.givers.repository.entity.Cause;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CauseService {

	Flux<Cause> all(); 
	Mono<Cause> get(String id);
	Flux<Cause> getByOwner(String owner);
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
	Flux<Cause> getUserParticipation(String id);
	Mono<Cause> attendToCause(Cause cause, String username);
}
