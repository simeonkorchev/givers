package com.givers.web;

import java.net.URI;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.givers.domain.CommentService;
import com.givers.repository.entity.Comment;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentRestController {

	private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
	private final CommentService service;
	
	public CommentRestController(CommentService service) {
		this.service = service;
	}
	
	@GetMapping
	public Publisher<Comment> getAll() {
		return this.service.all();
	}
	
	@GetMapping("{id}")
	public Publisher<Comment> getById(@PathVariable("id") String id) {
		return this.service.get(id);
	}
	
	@PostMapping
	public Mono<ResponseEntity<Comment>> create(@RequestBody Comment comment) {
		return this.service.create(comment.getContent(), comment.getOwner(), comment.getCauseId())
		.map(c -> ResponseEntity.created(URI.create("/comments/" + c.getId()))
				.contentType(this.mediaType)
				.build());
	}
	
	@PutMapping("{id}")
	public Mono<ResponseEntity<Comment>> updateById(@PathVariable String id, @RequestBody Comment comment) {
		return Mono
			.just(comment)
			.flatMap(updated -> this.service.update(id, updated.getContent(), updated.getOwner(), updated.getCauseId()))
			.map(r -> ResponseEntity.created(URI.create("/comments/id" + r.getId()))
					.contentType(this.mediaType)
					.build());
	}
	
	@DeleteMapping("{id}")
	public Publisher<Comment> deleteById(@PathVariable String id) {
		return this.service.delete(id);
	}
}
