package com.givers.web;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.givers.domain.core.CauseService;
import com.givers.repository.entity.Cause;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/causes")
public class CauseRestController {
	private static final String DEFAULT_AVATAR = "placeholder.jpg";
	private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
	private final CauseService causeService;

	@Value("${images.mount}")
	private String imagesMount;
	
	CauseRestController(CauseService service) {
		this.causeService = service;
	}
	
	@GetMapping
	@PreAuthorize("hasRole('USER')")
	Publisher<Cause> getAll() {
		log.info("Getting all causes");
		return this.causeService.all();
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	Publisher<Cause> getById(@PathVariable("id") String id) {
		return this.causeService.get(id);
	}
	
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	Mono<ResponseEntity<Cause>> create(@RequestBody Cause c) {
		log.info("Creating cause: " + c.toString());
		return this.causeService.create(c.getName(),c.getOwner(),
				c.getLocation(),c.getDescription(), c.getCauseType(), c.getImagePath(), c.getTime(), c.getCommentIds(), c.getParticipantIds())
				.map(r -> ResponseEntity.created(URI.create("/causes/" + r.getId()))
						.contentType(mediaType)
						.body(r));	
	}
	
	@PostMapping("/upload/{causeId}")
	@PreAuthorize("hasRole('USER')")
	Mono<ResponseEntity<String>> process(@PathVariable("causeId") String causeId, @RequestPart("file") Flux<FilePart> filePartFlux) {
		log.info("Uploading image for cause: ", causeId);
		return filePartFlux
				.flatMap(it ->  it.transferTo(Paths.get(this.imagesMount + "/"+ causeId)))
		        .then(Mono.just(
		        	ResponseEntity
		        		.ok()
		        		.contentType(mediaType)
		        		.build()
		        ));
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	Publisher<Cause> deleteById(@PathVariable("id") String id) {
		return this.causeService.delete(id);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	Mono<ResponseEntity<Cause>> updateById(@PathVariable("id") String id, @RequestBody Cause cause) {
		return Mono
				.just(cause)
				.flatMap(c -> this.causeService.update(c.getId(), c.getName(),c.getOwner(),
						c.getLocation(),c.getDescription(), c.getCauseType(), c.getImagePath(), c.getTime(), c.getCommentIds(), c.getParticipantIds()))
				.map(c -> ResponseEntity
						.ok()
						.contentType(mediaType)
						.build());
	}
	
	@GetMapping("/image/{causeId}")
//	@PreAuthorize("hasRole('USER')")
	Mono<ResponseEntity<InputStreamResource>> getImage(@PathVariable("causeId") String causeId) throws FileNotFoundException {
		final File imgFile = new File(this.imagesMount + "/" + causeId);
		InputStream imgStream;
		try {
			imgStream = new DataInputStream(new FileInputStream(imgFile));
		} catch (FileNotFoundException e) {
			ClassLoader classLoader = getClass().getClassLoader();
		       URL resource = classLoader.getResource(DEFAULT_AVATAR);
			imgStream = new DataInputStream(new FileInputStream(resource.getFile()));
		}
		
		return Mono
				.just(new InputStreamResource(imgStream))
				.map(isr -> ResponseEntity
						.ok()
						.contentType(MediaType.IMAGE_PNG)
						.body(isr)
				);
	}
	
	@PutMapping("/attend/{username}")
	@PreAuthorize("hasRole('USER')")
	Mono<ResponseEntity<Cause>> attendToCause(@PathVariable("username") String username, @RequestBody Cause cause) {
		return Mono
				.just(cause)
				.flatMap(c -> this.causeService.attendToCause(cause, username))
				.map(c -> ResponseEntity
							.ok()
							.contentType(mediaType)
							.build());
	}
	
	@GetMapping("/own/{owner}")
	@PreAuthorize("hasRole('USER')")
	Publisher<Cause> findOwnCauses(@PathVariable("owner") String owner) {
		return this.causeService.getByOwner(owner);
	}
	
	@GetMapping("/attend/{owner}")
	@PreAuthorize("hasRole('USER')")
	Publisher<Cause> getUserParticipation(@PathVariable("owner") String owner) {
		return this.causeService.getUserParticipation(owner);
	}
}
