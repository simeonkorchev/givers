package com.givers.initializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.givers.domain.CauseService;
import com.givers.domain.CommentService;
import com.givers.repository.database.CauseRepository;
import com.givers.repository.database.CollectorRepository;
import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Authority;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.CauseType;
import com.givers.repository.entity.EventType;
import com.givers.repository.entity.Log;
import com.givers.repository.entity.Role;
import com.givers.repository.entity.User;
import com.givers.security.AuthoritiesConstants;

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
	private Generator<Cause> causeGenerator;
	private Generator<Log> logGenerator;
	private Generator<User> userGenerator;

	private List<Cause> allCauses;
	private Map<String, List<Cause>> causeTypesToCauses;
	private Map<String, List<Cause>> locationsToCauses;
	private static final String CITIES_FILE_PATH = "/Users/i340033/Downloads/selishtabg/gradove.csv";

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
		System.out.println("Start generating data...");
		// this.userRepository.findAll().subscribe(new Subscriber<User>() {

		// 	@Override
		// 	public void onSubscribe(Subscription s) {
		// 		s.request(Long.MAX_VALUE);
		// 	}

		// 	@Override
		// 	public void onNext(User t) {
		// 		ownerIds.add(t.getId());
		// 		allUsers.add(t);
		// 	}

		// 	@Override
		// 	public void onError(Throwable t) {
		// 	}

		// 	@Override
		// 	public void onComplete() {
		// 	}

		// });
		// this.locations = readCitiesCsv();
		// this.causeTypes = Stream.of(CauseType.values()).map(CauseType::name).collect(Collectors.toList());
		// this.logTypes = Stream.of(EventType.values()).map(EventType::name).collect(Collectors.toList());
		// this.allCauses = new ArrayList<>();

		System.out.println();
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
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
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

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
					public void onError(Throwable t) {
						// TODO Auto-generated method stub
						
					}

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
		
//		initLogs(10000);
//		initUsers();

//	    this.causeRepo.saveAll(createCauses(6000)).subscribe();

		// repository
		// .deleteAll()
		// .thenMany(
		// Flux
		// .just("a","b","c","d")
		// .map(name -> new User(UUID.randomUUID().toString(),name+"tests@abv.bg",
		// name+"test",
		// encoder.encode("test"), null, true, allRoles, "", 0))
		// .flatMap(repository::save)
		// )
		// .thenMany(repository.findAll())
		// .subscribe(user -> log.info("saving " + user.toString()));

	}

	private void initLogs(int limit) {
		this.causeRepo.findAll().subscribe(new Subscriber<Cause>() {

			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(Cause t) {
				allCauses.add(t);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println(t.getStackTrace());
			}

			@Override
			public void onComplete() {
//				initCauseTypesToCauses();
//				initLocationsToCauses();
//				List<Log> logs = generateLogs(limit);
			}

		});
	}

//	private void initUsers() {
//		List<Role> allRoles = new ArrayList<>();
//    	allRoles.add(Role.ROLE_USER);
//    	List<String> cities = readCitiesCsv();
//    	System.out.println();
//    	Random r = new Random();
//    	String userPassword = "abcd1234";
//    	int causesCount = 100;
//    	String mailSuffix = "@abv.bg";
//    	String encodedPwd = getEncodedPassword(userPassword);
//    	int count = 0;
//    	List<Authority> authorities = new ArrayList<>();
//    	authorities.add(new Authority(AuthoritiesConstants.USER));
//    	List<User> users = new ArrayList<>();
//    	for(int i = 0; i < firstNames.length; i++) {
//    		int firstNameIndex = r.nextInt(firstNames.length - 1);
//    		int lastNameIndex = r.nextInt(lastNames.length - 1);
//    		String firstName = firstNames[i];
//    		count++;
//    		for(int j = 0; j < lastNames.length; j++) {
//    			count++;
//	    		String lastName = lastNames[j];
//	    		String firstNameLatin = firstNamesLatin[i];
//	    		String lastNameLatin = lastNamesLatin[j];
//	    		String email = firstNameLatin.toLowerCase() +
//	    						"." +
//	    						lastNameLatin.toLowerCase()+ 
//	    						mailSuffix;
//				String username = new String(firstNameLatin + lastNameLatin).toLowerCase();
//				users.add(new User(null, email, username, firstNameLatin, lastNameLatin, encodedPwd, null, null, null, 0, authorities));
////	    		
////				this.userRepositor/y
////	    			.findAll()
////	    			.flatMap(u -> {
////	    				return this.userRepository.save(new User(u.getId(), u.getEmail(), username, u.getFirstName(), u.getLastName(), u.getPassword(), u.getCauseIds(), u.getCommentIds(), u.getPhotoPath(), u.getHonor(), u.getAuthorities()));
////	    			}).subscribe();
//    		}
//    	}
//    	this.userRepository.saveAll(users).subscribe();
//	}


}
