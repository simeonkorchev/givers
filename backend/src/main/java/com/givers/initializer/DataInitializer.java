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

import com.givers.domain.core.CauseService;
import com.givers.domain.core.CommentService;
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
	private static final int DEFAULT_CAUSES_COUNT = 5000;
	private static final int DEFAULT_LOGS_COUNT = 15000;
	private static final int DEFAULT_USER_INTERACTIONS_COUNT = 10;
	
	private final CommentService commentService;
	private final UserRepository userRepository;
	private final CauseRepository causeRepo;
	private final CauseService causeService;
	private final CollectorRepository collectorRepo;
	private final PasswordEncoder encoder;
	private final CitiesReader citiesReader;
	
	@Value("${init.data}")
	private String shouldInitData;
	@Value("${causes.count}")
	private String causesCountConfig;
	@Value("${logs.count}")
	private String logsCountConfig;
	@Value("${user.interactions.count}")
	private String userInteractionsCountConfig;
	
	private int causesCount;
	private int logsCount;
	private int userInteractionsCount;

	private Generator<Cause> causeGenerator;
	private Generator<Log> logGenerator;
	private Generator<User> userGenerator;

	@Autowired
	public DataInitializer(CommentService commentService, CauseService causeService, UserRepository repository,
			CauseRepository causeRepo, CollectorRepository collectorRepository, PasswordEncoder encoder) {
		this.commentService = commentService;
		this.causeService = causeService;
		this.userRepository = repository;
		this.encoder = encoder;
		this.causeRepo = causeRepo;
		this.collectorRepo = collectorRepository;
		this.citiesReader = new CitiesReader();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		this.causesCount = isNullOrEmpty(causesCountConfig) ? DEFAULT_CAUSES_COUNT : Integer.parseInt(causesCountConfig);
		this.logsCount = isNullOrEmpty(logsCountConfig) ? DEFAULT_LOGS_COUNT : Integer.parseInt(logsCountConfig);
		this.userInteractionsCount = isNullOrEmpty(userInteractionsCountConfig) ? DEFAULT_USER_INTERACTIONS_COUNT : Integer.parseInt(userInteractionsCountConfig);
		if(!this.shouldInitData.equalsIgnoreCase("true")) {
			return;
		}
		System.out.println("Generating data...");
		List<String> cities = this.citiesReader.readCities();
		this.userGenerator = new UserGenerator(cities, this.encoder);
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
				List<Cause> causes = causeGenerator.generate(causesCount);
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
								commentService,
								userInteractionsCount
						);
						List<Log> logs = logGenerator.generate(logsCount);
						collectorRepo
							.saveAll(logs)
							.subscribe();
					}
				});
			}	
		});
	}

	private static boolean isNullOrEmpty(String data) {
		return data == null || data.isEmpty();
	}
}
