package com.givers.domain;

import org.springframework.stereotype.Service;

import com.givers.domain.core.PasswordResetTokenService;
import com.givers.repository.database.PasswordResetTokenRepository;
import com.givers.repository.entity.PasswordResetToken;
import com.givers.repository.entity.User;

import reactor.core.publisher.Mono;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
	
	private final PasswordResetTokenRepository repository;
	
	public PasswordResetTokenServiceImpl(PasswordResetTokenRepository repository) {
		this.repository = repository;
	}
	
	public Mono<PasswordResetToken> get(String id) {
		return this.repository.findById(id);
	}
	
	public Mono<PasswordResetToken> create(User user, String token) {
		return this.repository.save(new PasswordResetToken(token, user));
	}
	
	public Mono<PasswordResetToken> delete(String id) {
		return this.repository
				.findById(id)
				.flatMap(token -> this.repository.deleteById(token.getId())
				.thenReturn(token));
	}
 
}
