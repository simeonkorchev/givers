package com.givers.domain.core;

import java.util.List;

import com.givers.repository.entity.Authority;
import com.givers.repository.entity.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
	Flux<User> all();
	Mono<User> get(String id);
	Mono<User> getByUsername(String username);
	Mono<User> getByEmail(String email);
	Mono<User> update(String id, String firstName, String lastName, String email, String username, String password, List<String> causes, List<String> commentIds, String photoPath, int honor, List<Authority> authorities);
	Mono<User> delete(String id);
	Mono<User> create(String firstName, String lastName, String email, String username, String password, List<String> causes, List<String> commentIds, String photoPath, int honor, List<Authority> authorities);
	Mono<User> changeUserPassword(String username, String oldPassword, String newPassword);
}
