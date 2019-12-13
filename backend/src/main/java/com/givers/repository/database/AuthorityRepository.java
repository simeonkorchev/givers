package com.givers.repository.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.givers.repository.entity.Authority;


@Repository
public interface AuthorityRepository extends ReactiveMongoRepository<Authority, String> {
}
