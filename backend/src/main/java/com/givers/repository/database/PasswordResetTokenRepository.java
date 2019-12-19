package com.givers.repository.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.givers.repository.entity.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends ReactiveMongoRepository<PasswordResetToken, String> {

}
