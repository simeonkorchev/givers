package com.givers.initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.givers.domain.CauseService;
import com.givers.domain.CausesResponse;
import com.givers.domain.CommentService;
import com.givers.domain.RecommendedCause;
import com.givers.domain.RecommenderService;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.CollectorRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.EventType;
import com.givers.repository.entity.Log;
import com.givers.repository.entity.User;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
	private final CommentService commentService;
	private final UserRepository userRepository;
	private final CauseRepository causeRepo;
	private final CauseService causeService;
	private final CollectorRepository collectorRepo;
	private final PasswordEncoder encoder;
	private final CitiesReader citiesReader;
	private final RecommenderService recService; //TODO delete this after test.
	@Value("init.data")
	private String shouldInitData;
	private Generator<Cause> causeGenerator;
	private Generator<Log> logGenerator;
	private Generator<User> userGenerator;

	@Autowired
	public DataInitializer(RecommenderService recService, CommentService commentService, CauseService causeService, UserRepository repository,
			CauseRepository causeRepo, CollectorRepository collectorRepository, PasswordEncoder encoder) {
		this.commentService = commentService;
		this.causeService = causeService;
		this.userRepository = repository;
		this.encoder = encoder;
		this.causeRepo = causeRepo;
		this.collectorRepo = collectorRepository;
		this.citiesReader = new CitiesReader();
		this.recService = recService;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		this.recService
			.recommend("olegpopov", 10)
			.subscribe(new Subscriber<RecommendedCause>() {

				@Override
				public void onSubscribe(Subscription s) {
					s.request(Long.MAX_VALUE);
				}

				@Override
				public void onNext(RecommendedCause t) {
					t.setId(t.get_id().getId());
					System.out.println("Recieved causes " + t.toString());
				}

				@Override
				public void onError(Throwable t) {
					System.out.println(t.getStackTrace().toString());
				}

				@Override
				public void onComplete() {
					System.out.println("Receiving done!");
				}
				
			});
		if(!this.shouldInitData.equalsIgnoreCase("true")) {
			return;
		}
		System.out.println("Generating data...");
		List<String> cities = this.citiesReader.readCities();
		this.userGenerator = new UserGenerator(cities, this.encoder, this.userRepository);
		List<User> users = this.userGenerator.generate(Integer.MAX_VALUE);
		List<User> savedUsers = new ArrayList<User>();
		
		this.userRepository.saveAll(users).subscribe(new Subscriber<User>() {

			@Override
			public void onSubscribe(Subscription s) {
				s.request(Integer.MAX_VALUE);
			}

			@Override
			public void onNext(User t) {
				savedUsers.add(t);
			}

			@Override
			public void onError(Throwable t) {}

			@Override
			public void onComplete() {
				causeGenerator = new CauseGenerator(savedUsers, cities);
				List<Cause> causes = causeGenerator.generate(5000);
				List<Cause> savedCauses = new ArrayList<>();
				causeRepo.saveAll(causes).subscribe(new Subscriber<Cause>() {

					@Override
					public void onSubscribe(Subscription s) {
						s.request(Integer.MAX_VALUE);
					}

					@Override
					public void onNext(Cause t) {
						savedCauses.add(t);
					}

					@Override
					public void onError(Throwable t) {}

					@Override
					public void onComplete() {
						logGenerator = new LogGenerator(
								(Supplier<String>) causeGenerator, 
								savedCauses, 
								savedUsers, 
								Stream.of(EventType.values()).map(EventType::name).collect(Collectors.toList()),
								causeService,
								commentService
						);
						List<Log> logs = logGenerator.generate(15000);
						collectorRepo
							.saveAll(logs)
							.subscribe();
					}
				});
			}
			
		});
	}
}
