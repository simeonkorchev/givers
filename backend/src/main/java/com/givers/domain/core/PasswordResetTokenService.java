package com.givers.domain.core;

import com.givers.repository.entity.PasswordResetToken;
import com.givers.repository.entity.User;

import reactor.core.publisher.Mono;

public interface PasswordResetTokenService {
	Mono<PasswordResetToken> get(String id);
	Mono<PasswordResetToken> create(User user, String token);
	Mono<PasswordResetToken> delete(String id);
}
