package com.givers.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/causes-blocking")
public class CauseBlockingRestController {
	private static final int DELAY_PER_ITEM_MS = 100;
	private final CauseBlockingRepository repo;

	public CauseBlockingRestController(CauseBlockingRepository repo) {
		this.repo = repo;
	}
	
	@GetMapping
	Iterable<Cause> getAll() throws InterruptedException {
		Thread.sleep(DELAY_PER_ITEM_MS * repo.count());
		return this.repo.findAll();
	}
}
