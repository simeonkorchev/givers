package com.givers.domain;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.givers.domain.core.UserService;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Authority;
import com.givers.repository.entity.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService{
	@SuppressWarnings("unused")
	private final ApplicationEventPublisher publisher; 
    private final UserRepository userRepository;
    private PasswordEncoder encoder;
    
	public UserServiceImpl(ApplicationEventPublisher publisher, UserRepository userRepository, PasswordEncoder encoder) {
		super();
		this.publisher = publisher;
		this.encoder = encoder;
		this.userRepository = userRepository;
	}
    
	public Flux<User> all() {
		return this.userRepository.findAll();
	}
	
	public Mono<User> get(String id) {
		return this.userRepository.findById(id);
	}
	
	public Mono<User> getByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}
	
	public Mono<User> getByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}
	
	public Mono<User> update(String id, String firstName, String lastName, String email, String username, String password, List<String> causes, List<String> commentIds, String photoPath, int honor, List<Authority> authorities) {
		return this.userRepository
				.findById(id)
				.map(u -> new User(id, email, username, firstName, lastName, password, causes, commentIds, photoPath, honor, authorities))
				.flatMap(this.userRepository::save);
	}
	
	public Mono<User> delete(String id) {
		return this.userRepository
				.findById(id)
				.flatMap(u -> this.userRepository.deleteById(u.getId()).thenReturn(u));
	}
	
	public Mono<User> create(String firstName, String lastName, String email, String username, String password, List<String> causes, List<String> commentIds, String photoPath, int honor, List<Authority> authorities)
			{
		String encodedPwd = encoder.encode(password);
		return this.userRepository
				.save(new User(null, email, username, firstName, lastName, encodedPwd, causes, commentIds, photoPath,honor, authorities));
				//.doOnSuccess(user -> this.publisher.publishEvent(new UserCreatedEvent(user)));
	}
	
	public Mono<User> changeUserPassword(String username, String oldPassword, String newPassword) {
		return this.userRepository.findByUsername(username)
			.switchIfEmpty(Mono.defer(this::raiseBadCredentials))
			.filter(u -> this.encoder.matches(oldPassword, u.getPassword()))
			.switchIfEmpty(Mono.defer(this::raiseBadCredentials))
			.flatMap(u ->  {
				u.setPassword(encoder.encode(newPassword));
				return this.userRepository.save(u);	
			});
	}

	private <T> Mono<T> raiseBadCredentials() {
		return Mono.error(new BadCredentialsException("Invalid username"));
	}
}
