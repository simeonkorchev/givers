package com.givers.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.givers.event.CauseCreatedEvent;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Cause;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public final class CauseServiceImpl implements CauseService {
    private final ApplicationEventPublisher publisher; 
    private final CauseRepository repository;
    private final UserRepository userRepository;
    
    public CauseServiceImpl(ApplicationEventPublisher publisher, CauseRepository repository, UserRepository userRepository) {
        this.publisher = publisher;
        this.repository = repository;
        this.userRepository = userRepository;
    }
    
    public Flux<Cause> all() {
    	return this.repository.findAll();
    }
    
    public Mono<Cause> get(String id) {
    	return this.repository.findById(id);
    }
    
    public Flux<Cause> getByOwnerId(String ownerId) {
    	return this.repository.findByOwnerId(ownerId);
    }
	
    public Mono<Cause> update(String id, String name, String userId, String location, String description, String causeType, Long time, List<String> commentIds, List<String> participantIds) {
    	//TODO check whether causeType is a known and expected value value
    	return this.repository.findById(id)
    			.map(c -> new Cause(id,name, userId, location, description, causeType, time, commentIds, participantIds))
    			.flatMap(this.repository::save);
    }
    
    public Mono<Cause> delete(String id) {
    	return this.repository
    			.findById(id)
    			.flatMap(c -> this.repository
    								.deleteById(c.getId())
    								.thenReturn(c)
    			);
    }
    
    public Mono<Cause> create(String name, String ownerId, String location, String description, String causeType, Long time, List<String> commentIds, List<String> participantIds) {
    	log.info("Creating cause: "+new Cause(null, name, ownerId, location, description, causeType, time, commentIds, participantIds).toString());
    	return this.repository
    			.save(new Cause(null, name, ownerId, location, description, causeType, time, commentIds, participantIds))
    			.doOnSuccess(c ->  {
    				log.info("Updating the user causes");
    				this.publisher.publishEvent(new CauseCreatedEvent(c));
    				this.userRepository
    					.findById(ownerId)
    					.switchIfEmpty(raiseIllegalState("Could not find user with id: "+ ownerId))
    					.subscribe(user -> {
    						user.setCauses(appendIdToList(user.getCauses(), c.getId()));
    						this.userRepository.save(user).subscribe();
    					});
    			});
    }
    

	public Flux<Cause> getUserParticipation(String ownerId) {
		return this.all()
				.filter(cause -> 
					containUsername(cause.getParticipantIds(), ownerId)
				);
	}
    
	private boolean containUsername(List<String> participantIds, String ownerId) {
		return participantIds.stream().anyMatch(id -> id.equals(ownerId));
	}

	public Mono<Cause> updateAttendanceList(Cause cause, String username) {
		return this.userRepository
				.findByUsername(username)
				.switchIfEmpty(raiseIllegalState("Could not find user with username: " + username))
				.filter(user -> {
					return user.getCauses().stream().anyMatch(causeId -> causeId.equals(cause.getId()));
				})
				.switchIfEmpty(Mono.empty())
				.doOnSuccess(user -> {
					log.info("Found user: " + user);
					user.setCauses(appendIdToList(user.getCauses(), cause.getId()));
					this.userRepository.save(user).subscribe();
				})
				.map(user -> {
					cause.setParticipantIds(appendIdToList(cause.getParticipantIds(), user.getId()));
					return user;
				})
				.then(this.repository.save(cause));	
	}
    
    private static List<String> appendIdToList(List<String> ids, String id) {
    	if(ids == null) {
    		ids = new ArrayList<>();
    	}
    	ids.add(id);
    	return ids;
    }
    
    private static <T> Mono<T> raiseIllegalState(String errorMsg) {
    	return Mono.error(new IllegalStateException(errorMsg));
    }
}
