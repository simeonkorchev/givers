package com.givers.domain;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.givers.domain.core.CollectorService;
import com.givers.repository.database.CollectorRepository;
import com.givers.repository.entity.EventType;
import com.givers.repository.entity.Log;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class CollectorServiceImpl implements CollectorService{

	private CollectorRepository collectorRepository;
	
	@Autowired
	public CollectorServiceImpl(CollectorRepository repository) {
		super();
		this.collectorRepository = repository;
	}

	@Override
	public Mono<Log> create(String username, String causeId, String eventType, String causeName) {
		if(!isEventTypeExpected(eventType)) {
			return raiseIllegalState("Unexpected event type");
		}
		return this.collectorRepository
				.save(new Log(null, username, causeId, eventType, causeName, System.currentTimeMillis()));
	}
	
	private static boolean isEventTypeExpected(String eventType) {
		return Arrays.stream(EventType.values()).anyMatch((e) -> e.name().equalsIgnoreCase(eventType));
	}
	
	private <T> Mono<T> raiseIllegalState(String errorMsg) {
		return Mono.error(new IllegalStateException(errorMsg));
	}

	@Override
	public Flux<Log> getByUsername(String username) {
		return this.collectorRepository.findByUsername(username);
	}
}
