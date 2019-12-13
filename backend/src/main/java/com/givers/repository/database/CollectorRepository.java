package com.givers.repository.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.givers.repository.entity.Log;

import reactor.core.publisher.Flux;

public interface CollectorRepository extends ReactiveMongoRepository<Log, String> {
	Flux<Log> findByUsername(String username);
	Flux<Log> findByCauseId(String causeId);
}
