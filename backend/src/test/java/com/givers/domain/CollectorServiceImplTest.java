package com.givers.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import com.givers.domain.core.CollectorService;
import com.givers.repository.entity.EventType;
import com.givers.repository.entity.Log;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import(CollectorServiceImpl.class)
public class CollectorServiceImplTest {
	private final CollectorService service;
	
	@Autowired
	public CollectorServiceImplTest(CollectorServiceImpl service) {
		this.service = service;
	}

	@Test
	public void create() {
		Mono<Log> log = this.service.create("TestUsername", "TestCauseId", EventType.ATTEND.name(), "TestCause");
		
		StepVerifier
			.create(log)
			.expectNextMatches(l -> StringUtils.hasText(l.getId()) && 
								l.getUsername().equals("TestUsername") &&
								l.getCauseId().equals("TestCauseId") &&
								l.getEventType().equals(EventType.ATTEND.name()))
			.verifyComplete();
	}
	
	@Test
	public void givenInvalidEventType_thenExceptionIsThrown() {
		Mono<Log> log = this.service.create("TestUsername", "TestCauseId", "InvalidEventType", "TestCause");
		
		StepVerifier
			.create(log)
			.expectError(IllegalStateException.class);
	}
}
