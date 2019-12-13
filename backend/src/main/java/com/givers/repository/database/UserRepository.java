package com.givers.repository.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.givers.repository.entity.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUsername(String username);
    Mono<User> findByEmail(String email);
}