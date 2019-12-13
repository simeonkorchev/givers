package com.givers.repository.database;
//package com.givers.repository.repositories;
//
//import org.springframework.data.r2dbc.repository.R2dbcRepository;
//import org.springframework.data.r2dbc.repository.query.Query;
//import org.springframework.stereotype.Repository;
//
//import com.givers.repository.entities.User;
//
//import reactor.core.publisher.Mono;
//
//@Repository
//public interface UserMySQLRepository extends R2dbcRepository<User, String> {
//	
//	@Query("SELECT * FROM users WHERE username = $1")
//	Mono<User> findByUsername(String username);
//	
//	@Query("SELECT * FROM users WHERE email = $1")
//	Mono<User> findByEmail(String email);
//}
