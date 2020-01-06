package com.givers.web;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.givers.domain.core.CollectorService;
import com.givers.repository.entity.Log;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
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
	public Mono<ResponseEntity<Log>> collectUserBehavior(@RequestBody Log l) {
		log.info("Creating log: " + l.toString());
		return this.service
				.create(l.getUsername(), l.getCauseId(), l.getEventType(), l.getCauseName())
				.map(lg -> ResponseEntity.created(URI.create("/collect/" + lg.getId()))
						.contentType(mediaType)
						.build());	
	}
	
	
	@GetMapping("/{username}")
	@PreAuthorize("hasRole('USER')")
	public Flux<Log> getByUsername(@PathVariable("username") String username) {
		return this.service.getByUsername(username);
	}
	
}
