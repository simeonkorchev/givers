package com.givers.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.givers.domain.core.CauseService;
import com.givers.domain.core.CommentService;
import com.givers.repository.entity.Cause;
import com.givers.repository.entity.Log;
import com.givers.repository.entity.User;

public class LogGenerator implements Generator<Log>{
	private final Random random = new Random();

	private Map<String, List<Cause>> causeTypesToCauses;
	private Map<String, List<Cause>> locationsToCauses;
	private Supplier<String> causeTypeSupplier;
	private List<Cause> allCauses;
	private List<User> allUsers;
	private List<String> logTypes;
	private CauseService causeService;
	private CommentService commentService;
	private final int userInteractionsCount;

	public LogGenerator(Supplier<String> causeTypeSupplier, List<Cause> allCauses, List<User> allUsers, List<String> logTypes,
			CauseService causeService, CommentService commentService, int userInteractionsCount) {
		super();

		this.causeTypeSupplier = causeTypeSupplier;
		this.allCauses = allCauses;
		this.allUsers = allUsers;
		this.logTypes = logTypes;
		this.causeService = causeService;
		this.commentService = commentService;
		this.userInteractionsCount = userInteractionsCount;
		initCauseTypesToCauses();
		initLocationsToCauses();
	}
	
	// Prepare a valid tendentions in the logs
	// By which the user will have a certain preferences for certain causes
	// The preferences could be not only for cause type but could be for cause
	// location also
	// Think of combining the two parameters and create a logs based on them.
	// Flow:
	// A random user, which is not owner of the cause
	// Makes userInteractionsCount random interactios (attend, view or comment)
	// With causes from the same type and location (if possible)
	// If the location have no other causes, then an other random location is chosen
	// All this is being done for the half of the users.
	// If the interaction is attend or comment, then the cause and user needs to be updated accordingly
	@Override
	public List<Log> generate(int count) {
		List<Log> logs = new ArrayList<>(count);
		for (int j = 0; j < count;) {
			User user = getRandomUser();
			String causeType = this.causeTypeSupplier.supply();
			Cause cause = getRandomCauseWithType(causeType);
			boolean isStart = true;
			for (int i = 0; i < this.userInteractionsCount; i++, j++) {
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
							this.causeService.attendToCause(cause, user.getUsername()).subscribe();
							logs.add(new Log(null,user.getUsername(),cause.getId(), logType, cause.getName(), System.currentTimeMillis()));
							logs.add(new Log(null,user.getUsername(),cause.getId(), "CAUSE_DETAILS_VIEWED", cause.getName(), System.currentTimeMillis()));
						break;
					}
					case "CAUSE_DETAILS_VIEWED": {
							logs.add(new Log(null,user.getUsername(),cause.getId(), logType, cause.getName(), System.currentTimeMillis()));
						break;
					}
					case "CAUSE_TYPE_VIEWED": {
							logs.add(new Log(null,user.getUsername(),cause.getId(), logType, cause.getName(), System.currentTimeMillis()));
						break;
					}
					case "COMMENT_CREATED": {
							this.commentService.create("Харесвам каузата и бих желал да участвам. Може ли малко повече информация за нея?", user.getUsername(), cause.getId()).subscribe();
							logs.add(new Log(null,user.getUsername(),cause.getId(), "CAUSE_DETAILS_VIEWED", cause.getName(), System.currentTimeMillis()));
							logs.add(new Log(null,user.getUsername(),cause.getId(), logType, cause.getName(), System.currentTimeMillis()));
						break;
					}
				}
			}

		}
		return logs;
	}
	
	
	private String getRandomLogType() {
		return logTypes.get(random.nextInt(logTypes.size()));
	}

	private Cause getRandomCauseWithType(String type) {
		List<Cause> causes = causeTypesToCauses.get(type);
		return causes.get(random.nextInt(causes.size()));
	}
	
	private Cause getRandomCauseWithLocation(String location) {
		List<Cause> causes = locationsToCauses.get(location);
		return causes.get(random.nextInt(causes.size()));
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
	
	private User getRandomUser() {
		return allUsers.get(random.nextInt(allUsers.size()));
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
}
