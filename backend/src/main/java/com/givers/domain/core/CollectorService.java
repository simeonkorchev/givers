package com.givers.domain.core;

import com.givers.repository.entity.Log;

import reactor.core.publisher.Mono;

public interface CollectorService {
	Mono<Log> create(String username, String causeId, String eventType, String causeName);
}
