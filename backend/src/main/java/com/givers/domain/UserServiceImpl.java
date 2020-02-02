package com.givers.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.givers.domain.core.UserService;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Authority;
import com.givers.repository.entity.User;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private PasswordEncoder encoder;
    
    @Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
		super();
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
		return this.userRepository.findByUsername(username.toLowerCase());
	}
	
	public Mono<User> getByEmail(String email) {
		return this.userRepository.findByEmail(email.toLowerCase());
	}
	
	public Mono<User> update(String id, String firstName, String lastName, String email, String username, String password, List<String> involvedCauses, List<String> ownCauses, List<String> commentIds, String photoPath, int honor, List<Authority> authorities) {
		return this.userRepository
				.findById(id)
				.map(u -> new User(id, email.trim().toLowerCase(), username.trim().toLowerCase(), firstName, lastName, password, involvedCauses, ownCauses, commentIds, photoPath, honor, authorities))
				.flatMap(this.userRepository::save);
	}
	
	public Mono<User> delete(String id) {
		return this.userRepository
				.findById(id)
				.flatMap(u -> this.userRepository
						.deleteById(u.getId())
						.thenReturn(u)
				);
	}
	
	public Mono<User> create(String firstName, String lastName, String email, String username, String password, List<String> involvedCauses, List<String> ownCauses, List<String> commentIds, String photoPath, int honor, List<Authority> authorities)
			{
		String encodedPwd = encoder.encode(password);
		log.info("Creating user: " + new User(null, email.toLowerCase(), username.toLowerCase(), firstName, lastName, encodedPwd, involvedCauses, ownCauses, commentIds, photoPath,honor, authorities));
		return this.userRepository
				.save(new User(null, email.toLowerCase(), username.toLowerCase(), firstName, lastName, encodedPwd, involvedCauses, ownCauses, commentIds, photoPath,honor, authorities));
				//.doOnSuccess(user -> this.publisher.publishEvent(new UserCreatedEvent(user)));
	}
	
	public Mono<User> changeUserPassword(String username, String oldPassword, String newPassword) {
		return this.userRepository.findByUsername(username)
			.switchIfEmpty(Mono.defer(this::raiseBadCredentials))
			.filter(u -> this.encoder.matches(oldPassword, u.getPassword()))
			.switchIfEmpty(Mono.defer(this::raiseBadCredentials))
			.map(u -> new User(u.getId(), u.getEmail(), u.getUsername(), u.getFirstName(), u.getLastName(), this.encoder.encode(newPassword), u.getCauses(), u.getOwnCauses(), u.getCommentIds(),u.getPhotoPath(), u.getHonor(), u.getAuthorities()))
			.flatMap(this.userRepository::save);
	}

	private <T> Mono<T> raiseBadCredentials() {
		return Mono.error(new BadCredentialsException("Invalid username"));
	}
}
