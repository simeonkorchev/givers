package com.givers.repository.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.givers.repository.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends ReactiveMongoRepository<PasswordResetToken, String> {

}
