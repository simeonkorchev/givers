package com.givers.web;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.givers.domain.CollectorService;
import com.givers.repository.entity.Log;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/collect", produces = MediaType.APPLICATION_JSON_VALUE)
public class CollectorRestController {
	
	private final CollectorService service;
	private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
	
	public CollectorRestController(CollectorService service) {
		this.service = service;
	}
	
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public Mono<ResponseEntity<Log>> collectUserBehavior(@RequestBody Log log) {
		return this.service
				.create(log.getUsername(), log.getCauseId(), log.getEventType(), log.getCauseName())
				.map(l -> ResponseEntity.created(URI.create("/collect/" + l.getId()))
						.contentType(mediaType)
						.build());	
	}
	
	
}
