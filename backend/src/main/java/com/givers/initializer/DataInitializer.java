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
	private final Random random = new Random();
	private final long minDay = LocalDate.of(2020, 3, 3).toEpochDay();
	private final long maxDay = LocalDate.of(2020, 12, 30).toEpochDay();

	private static final String ADULTS_TITLE = "Нека помогнем на възрастен човек в ";
	private static final String HOMELESS_TITLE = "Помогнете на бездомен човек в ";
	private static final String ANIMALS_TITLE = "Помогнете на животинка в ";
	private static final String CHILDREN_TITLE = "Помогнете на дете в нужда в ";
	private static final String NATURE_TITLE = "Да спасим и опазим природата в ";

	private List<String> ownerIds;
	private List<String> locations;
	private List<String> causeTypes;
	private List<String> logTypes;

	private final String[] firstNames = { "Стефан","Костадин", "Константин", "Павел", "Милен", "Галин", "Марин", "Веселин", "Илия", "Вельо", "Бисер", "Антоан", "Анатоли", "Асен", "Кирил", "Методи", "Симеон", "Георги", "Николай", "Никола", "Андрей",
			"Панталеймон", "Петър", "Олег", "Александър", "Мартин", "Ангел", "Серафим", "Марио", "Ивайло", "Иван",
			"Ивелин", "Христо", "Росен", "Янко", "Михаил", "Красимир", "Васил" };
	private final String[] firstNamesLatin = { "Stefan","Kostadin", "Konstantin", "Pavel", "Milen", "Galin", "Marin", "Veselin","Iliq", "Velyo","Biser", "Antoan", "Anatoli", "Asen", "Kiril", "Metodi", "Simeon", "Georgi", "Nikolay", "Nikola", "Andrey",
			"Pantaleimon", "Petar", "Oleg", "Alexandar", "Martin", "Angel", "Serafim", "Mario", "Ivailo", "Ivan",
			"Ivelin", "Hristo", "Rosen", "Qnko", "Mihail", "Krasimir", "Vasil" };
	private final String[] lastNames = {"Стефанов", "Костадинов", "Константинов","Караджов","Хаджиев", "Павлов", "Цветков", "Маринов", "Веселинов", "Топалов", "Кузманов", "Илиев", "Велев", "Асенов", "Георгиев", "Барбаров", "Корчев", "Петев", "Иванов", "Петров", "Балев",
			"Тупаров", "Милев", "Златков", "Дечев", "Попов", "Боянов", "Михайлов", "Бакърджиев", "Василев", "Ангелов",
			"Кирилов", "Христов" };
	private final String[] lastNamesLatin = { "Stefanov", "Kostadinov", "Konstantinov", "Karadjov","Hadjiev","Pavlov", "Cvetkov", "Marinov", "Veselinov", "Topalov", "Kuzmanov", "Iliev", "Velev", "Asenov", "Georgiev", "Barbarov", "Korchev", "Petev", "Ivanov", "Petrov", "Balev",
			"Tuparov", "Milev", "Zlatkov", "Dechev", "Popov", "Boqnov", "Mihaylov", "Bakardzhiev", "Vasilev", "Angelov",
			"Kirilov", "Hristov" };
	private List<Cause> allCauses;
	private List<User> allUsers;
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
		this.ownerIds = new ArrayList<>();
		this.allUsers = new ArrayList<>();
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

	private void initLocationsToCauses() {
		locationsToCauses = new HashMap<>();
		allCauses.forEach(cause -> {
			String location = cause.getLocation();
			if (!locationsToCauses.containsKey(location)) {
				List<Cause> causes = new ArrayList<>();
				causes.add(cause);
				locationsToCauses.put(location, causes);
				return;
			}
			List<Cause> causesForLocation = this.locationsToCauses.get(location);
			causesForLocation.add(cause);
			locationsToCauses.put(location, causesForLocation);
		});
	}

	private void initCauseTypesToCauses() {
		causeTypesToCauses = new HashMap<>();
		allCauses.forEach(cause -> {
			String type = cause.getCauseType();
			if (!causeTypesToCauses.containsKey(type)) {
				List<Cause> causes = new ArrayList<>();
				causes.add(cause);
				causeTypesToCauses.put(type, causes);
				return;
			}
			List<Cause> causesForType = this.causeTypesToCauses.get(type);
			causesForType.add(cause);
			causeTypesToCauses.put(type, causesForType);
		});
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println();
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
				initCauseTypesToCauses();
				initLocationsToCauses();
				List<Log> logs = generateLogs(limit);
			}

		});
	}

	private void initUsers() {
		List<Role> allRoles = new ArrayList<>();
    	allRoles.add(Role.ROLE_USER);
    	List<String> cities = readCitiesCsv();
    	System.out.println();
    	Random r = new Random();
    	String userPassword = "abcd1234";
    	int causesCount = 100;
    	String mailSuffix = "@abv.bg";
    	String encodedPwd = getEncodedPassword(userPassword);
    	int count = 0;
    	List<Authority> authorities = new ArrayList<>();
    	authorities.add(new Authority(AuthoritiesConstants.USER));
    	List<User> users = new ArrayList<>();
    	for(int i = 0; i < firstNames.length; i++) {
    		int firstNameIndex = r.nextInt(firstNames.length - 1);
    		int lastNameIndex = r.nextInt(lastNames.length - 1);
    		String firstName = firstNames[i];
    		count++;
    		for(int j = 0; j < lastNames.length; j++) {
    			count++;
	    		String lastName = lastNames[j];
	    		String firstNameLatin = firstNamesLatin[i];
	    		String lastNameLatin = lastNamesLatin[j];
	    		String email = firstNameLatin.toLowerCase() +
	    						"." +
	    						lastNameLatin.toLowerCase()+ 
	    						mailSuffix;
				String username = new String(firstNameLatin + lastNameLatin).toLowerCase();
				users.add(new User(null, email, username, firstNameLatin, lastNameLatin, encodedPwd, null, null, null, 0, authorities));
//	    		
//				this.userRepositor/y
//	    			.findAll()
//	    			.flatMap(u -> {
//	    				return this.userRepository.save(new User(u.getId(), u.getEmail(), username, u.getFirstName(), u.getLastName(), u.getPassword(), u.getCauseIds(), u.getCommentIds(), u.getPhotoPath(), u.getHonor(), u.getAuthorities()));
//	    			}).subscribe();
    		}
    	}
    	this.userRepository.saveAll(users).subscribe();
	}

	private List<Log> generateLogs(int limit) {
		List<Log> logs = new ArrayList<>(limit);
		for (int j = 0; j < limit;) {
			// TODO figure out how to prepare a valid tendentions in the logs
			// By which the user will have a certain preferences for certain causes
			// The preferences could be not only for cause type but could be for cause
			// location also
			// Think of combining the two parameters and create a logs based on them.
			// Flow:
			// A random user, which is not owner of the cause
			// Makes 5 random interactios (attend, view or comment)
			// With 5 causes from the same type and location (if possible)
			// If the location have no other causes, then an other random location is chosen
			// All this is being done for the half of the users
			// If the interaction is attend or comment, then the cause and user needs to be
			// updated accordingly
			User user = getRandomUser();
			String causeType = getRandomCauseType();
			Cause cause = getRandomCauseWithType(causeType);
			System.out.println("Selected cause with type " + causeType);
			System.out.println("Selected user " + user.getUsername());
			boolean isStart = true;
			for (int i = 0; i < 5; i++, j++) {
				if (!isStart) {
					Cause candidate = getRandomCauseWithLocation(cause.getLocation());
					if (candidate == null || candidate.getName().equals(cause.getName())) {
						System.out.println("Cause with that location does not exist");
						System.out.println("Getting another cause with type " + causeType);
						cause = getRandomCauseWithType(causeType);
					} else {
						cause = candidate;
					}
				}
				isStart = false;
				String logType = getRandomLogType();

				switch (logType) {
				case "ATTEND": {
					// in case that attend is the type, then for sure thte user is also viewed the
					// cause
						this.causeService.updateAttendanceList(cause, user.getUsername()).subscribe();
						this.collectorRepo.save(new Log(null,user.getUsername(),cause.getId(), logType, cause.getName(), System.currentTimeMillis())).subscribe();
						this.collectorRepo.save(new Log(null,user.getUsername(),cause.getId(), "CAUSE_DETAILS_VIEWED", cause.getName(), System.currentTimeMillis())).subscribe();
					System.out.println("Saving log with type " + logType + " for user " + user.getUsername()
							+ " and cause " + cause.getName());
					System.out.println("Updating the cause and user..");
					break;
				}
				case "CAUSE_DETAILS_VIEWED": {
					System.out.println("Saving log with type " + logType + " for user " + user.getUsername()
							+ " and cause " + cause.getName());
						this.collectorRepo.save(new Log(null,user.getUsername(),cause.getId(), logType, cause.getName(), System.currentTimeMillis())).subscribe();
					break;
				}
				case "CAUSE_TYPE_VIEWED": {
					System.out.println("Saving log with type " + logType + " for user " + user.getUsername()
							+ " and cause " + cause.getName());
						this.collectorRepo.save(new Log(null,user.getUsername(),cause.getId(), logType, cause.getName(), System.currentTimeMillis())).subscribe();
					break;
				}
				case "COMMENT_CREATED": {
					System.out.println("Saving log with type " + logType + " for user " + user.getUsername()
							+ " and cause " + cause.getName());
					System.out.println("Creating comment, updating cause and user..");
						this.commentService.create("Харесвам каузата и бих желал да участвам. Може ли малко повече информация за нея?", user.getUsername(), cause.getId()).subscribe();
						this.collectorRepo.save(new Log(null,user.getUsername(),cause.getId(), "CAUSE_DETAILS_VIEWED", cause.getName(), System.currentTimeMillis())).subscribe();
						this.collectorRepo.save(new Log(null,user.getUsername(),cause.getId(), logType, cause.getName(), System.currentTimeMillis())).subscribe();
					break;
				}
				}
			}

		}
		return logs;
	}

	private Cause getRandomCauseWithType(String type) {
		List<Cause> causes = causeTypesToCauses.get(type);
		return causes.get(random.nextInt(causes.size()));
	}

	private Cause getRandomCauseWithLocation(String location) {
		List<Cause> causes = locationsToCauses.get(location);
		return causes.get(random.nextInt(causes.size()));
	}

	private String getRandomLogType() {
		return logTypes.get(random.nextInt(logTypes.size()));
	}

	@SuppressWarnings("unused")
	private List<Cause> createCauses(int limit) {
		List<Cause> causes = new ArrayList<>(limit);
		for (int i = 0; i < limit; i++) {
			String causeType = this.getRandomCauseType();
			String location = this.getRandomLocation();
			String ownerId = this.getRandomOwnerId();
			Long time = this.getRandomDate();
			System.out.println("Random owner id: " + ownerId);
			System.out.println("Random cause type: " + causeType);
			System.out.println("Random location: " + location);
			System.out.println("Random date: " + LocalDate.ofEpochDay(time));
			String title = "";
			switch (causeType) {
			case "HOMELESS":
				title = HOMELESS_TITLE + location;
				break;
			case "ANIMALS":
				title = ANIMALS_TITLE + location;
				break;
			case "CHILDREN":
				title = CHILDREN_TITLE + location;
				break;
			case "ADULTS":
				title = ADULTS_TITLE + location;
				break;
			case "NATURE":
				title = NATURE_TITLE + location;
				break;

			}
			causes.add(new Cause(null, title, ownerId, location, title, causeType, time, null, null));
		}
		return causes;
	}

	private String getEncodedPassword(String userPassword) {
		return this.encoder.encode(userPassword);
	}

	private String getRandomOwnerId() {
		int index = random.nextInt(ownerIds.size() - 1);
		if (index < 0) {
			index = 0;
		}
		return this.ownerIds.get(index);
	}

	private User getRandomUser() {
		return allUsers.get(random.nextInt(allUsers.size()));
	}

	private String getRandomLocation() {
		return locations.get(random.nextInt(locations.size() - 1));
	}

	private String getRandomCauseType() {
		return causeTypes.get(random.nextInt(causeTypes.size()));
	}

	private Long getRandomDate() {
		// to get the day use: LocalDate.ofEpochDay(long);
		return minDay + random.nextInt((int) (maxDay - minDay));
	}

	private final List<String> readCitiesCsv() {
		String line = "";
		BufferedReader br = null;
		List<String> cities = new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(CITIES_FILE_PATH));

			while ((line = br.readLine()) != null) {
				cities.add(line.trim().split(",")[0].replaceAll("\"", ""));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cities;
	}
}
