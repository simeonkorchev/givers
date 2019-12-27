package com.givers.repository.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.givers.repository.entity.Cause;

import reactor.core.publisher.Flux;

@Repository
public interface CauseRepository extends ReactiveMongoRepository<Cause, String>{
	Flux<Cause> findByOwner(String owner);
}
