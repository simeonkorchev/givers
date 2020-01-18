package com.givers.benchmark;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface CauseRepository extends ReactiveMongoRepository<Cause, String>{
	Flux<Cause> findByOwner(String owner);
}
