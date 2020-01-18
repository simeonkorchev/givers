package com.givers.benchmark;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@RestController
@RequestMapping(value = "/causes-nonblocking")
public class CauseNonBlockingRestController {
	private static final long DELAY_PER_ITEM_MS = 100;
	private final CauseRepository repo;

	@Autowired
	public CauseNonBlockingRestController(CauseRepository repo) {
		this.repo = repo;
	}
	
	@GetMapping
	Flux<Cause> getAll() {
		return this.repo.findAll().delayElements(Duration.ofMillis(DELAY_PER_ITEM_MS));
	}
}
