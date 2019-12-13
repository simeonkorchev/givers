package com.givers.domain;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.givers.repository.database.CollectorRepository;
import com.givers.repository.entity.EventType;
import com.givers.repository.entity.Log;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class CollectorService {

    private ReactiveMongoTemplate template;
	private CollectorRepository repository;
	
	@Autowired
	public CollectorService(ReactiveMongoTemplate template, CollectorRepository repository) {
		super();
		this.template = template;
		this.repository = repository;
	}

	public Mono<Log> create(String username, String causeId, String eventType, String causeName) {
		if(!isEventTypeExpected(eventType)) {
			return raiseIllegalState("Unexpected event type");
		}
		return this.repository
				.save(new Log(null, username, causeId, eventType, causeName, System.currentTimeMillis()));
	}
	
	private static boolean isEventTypeExpected(String eventType) {
		return Arrays.stream(EventType.values()).anyMatch((e) -> e.name().equalsIgnoreCase(eventType));
	}

	public Flux<Log> getEventsForUserAndCause(String id, String username, String causeId, String eventType) {
		return this.template
				.find(new Query(
						where("username").is(username)
						.and("causeId").is(causeId)
						.and("eventType").is(eventType)), 
					Log.class);
	}
	
	private <T> Mono<T> raiseIllegalState(String errorMsg) {
		return Mono.error(new IllegalStateException(errorMsg));
	}
}
