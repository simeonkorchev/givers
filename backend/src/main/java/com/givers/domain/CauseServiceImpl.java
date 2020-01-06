package com.givers.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.givers.domain.core.CauseService;
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
    
    public Flux<Cause> getByOwner(String owner) {
    	return this.repository.findByOwner(owner);
    }
	
    public Mono<Cause> update(String id, String name, String userId, String location, String description, String causeType, String imagePath, Long time, List<String> commentIds, List<String> participantIds) {
    	//TODO check whether causeType is a known and expected value value
    	return this.repository.findById(id)
    			.map(c -> new Cause(id,name, userId, location, description, causeType, imagePath, time, commentIds, participantIds))
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
    
    public Mono<Cause> create(String name, String owner, String location, String description, String causeType, String imagePath, Long time, List<String> commentIds, List<String> participantIds) {
    	log.info("Creating cause: "+new Cause(null, name, owner, location, description, causeType, imagePath, time, commentIds, participantIds).toString());
    	return this.repository
    			.save(new Cause(null, name, owner, location, description, causeType, imagePath, time, commentIds, participantIds))
    			.doOnSuccess(c ->  {
    				log.info("Updating the user causes");
    				this.publisher.publishEvent(new CauseCreatedEvent(c));
    				this.userRepository
    					.findByUsername(owner)
    					.switchIfEmpty(raiseIllegalState("Could not find user with id: "+ owner))
    					.subscribe(user -> {
    						user.setCauses(appendIdToList(user.getOwnCauses(), c.getId()));
    						this.userRepository.save(user).subscribe();
    					});
    			});
    }
    
	public Flux<Cause> getUserParticipation(String owner) {
		return this.userRepository
				.findByUsername(owner)
				.switchIfEmpty(raiseIllegalState("Could not find user with id: "+ owner))
				.filter(u -> u.getCauses() != null)
				.flatMapMany(u -> {
					return this.repository.findAllById(u.getCauses());
				});
	}

	public Mono<Cause> attendToCause(Cause cause, String username) {
		return this.userRepository
				.findByUsername(username)
				.switchIfEmpty(raiseIllegalState("Could not find user with username: " + username))
				.filter(user -> user.getCauses() == null ||
							!user.getCauses().stream().anyMatch(causeId -> causeId.equals(cause.getId())))
				.switchIfEmpty(Mono.empty())
				.doOnSuccess(user -> {
					if(user == null) {
						return;
					}
					log.info("Found user: " + user);
					user.setCauses(appendIdToList(user.getCauses(), cause.getId()));
					this.userRepository.save(user).subscribe();
				})
				.map(user -> {
					log.info("Updating cause participant ids..");
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
