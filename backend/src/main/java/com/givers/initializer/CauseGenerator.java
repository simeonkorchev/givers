package com.givers.initializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.givers.repository.entity.Cause;
import com.givers.repository.entity.CauseType;
import com.givers.repository.entity.User;

public class CauseGenerator implements Generator<Cause>, Supplier<String> {
	private final Random random = new Random();
	private final long minDay = LocalDate.of(2020, 3, 3).toEpochDay();
	private final long maxDay = LocalDate.of(2020, 12, 30).toEpochDay();
	private final List<User> owners;

	private static final String ADULTS_TITLE = "Нека помогнем на възрастен човек в ";
	private static final String HOMELESS_TITLE = "Помогнете на бездомен човек в ";
	private static final String ANIMALS_TITLE = "Помогнете на животинка в ";
	private static final String CHILDREN_TITLE = "Помогнете на дете в нужда в ";
	private static final String NATURE_TITLE = "Да спасим и опазим природата в ";

	private List<String> locations;
	private List<String> causeTypes;
	
	public CauseGenerator(List<User> owners, List<String> locations) {
		this.owners = owners;
		this.locations = locations;
		this.causeTypes = Stream.of(CauseType.values()).map(CauseType::name).collect(Collectors.toList());
	}

	@Override
	public List<Cause> generate(int limit) {
		List<Cause> causes = new ArrayList<>(limit);
		for (int i = 0; i < limit; i++) {
			String causeType = this.getRandomCauseType();
			String location = this.getRandomLocation();
			String ownerId = this.owners.get(this.random.nextInt(this.owners.size())).getId();
			Long time = this.getRandomDate();
			System.out.println("Random owner id: " + ownerId);
			System.out.println("Random cause type: " + causeType);
			System.out.println("Random location: " + location);
			System.out.println("Random date: " + LocalDate.ofEpochDay(time));
			System.out.println("The seconds are: "+ time);
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
	
	private String getRandomLocation() {
		return locations.get(random.nextInt(locations.size()));
	}

	public String getRandomCauseType() {
		return causeTypes.get(random.nextInt(causeTypes.size()));
	}

	private Long getRandomDate() {
		// to get the day use: LocalDate.ofEpochDay(long);
		return minDay + random.nextInt((int) (maxDay - minDay));
	}

	@Override
	public String supply() {
		return causeTypes.get(random.nextInt(causeTypes.size()));
	}
}
