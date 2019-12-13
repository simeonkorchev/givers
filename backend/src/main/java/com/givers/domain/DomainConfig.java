package com.givers.domain;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.UserRepository;

@Configuration
class DomainConfig {
	
//	@Bean
//	@Qualifier
//	static CauseService causeService(ApplicationEventPublisher publisher, CauseRepository repository, UserRepository userRepository) {
//		return new CauseServiceImpl(publisher, repository, userRepository);
//	}
//	
}
