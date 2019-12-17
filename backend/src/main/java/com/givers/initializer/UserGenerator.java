package com.givers.initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.givers.repository.database.UserRepository;
import com.givers.repository.entity.Authority;
import com.givers.repository.entity.Role;
import com.givers.repository.entity.User;
import com.givers.security.AuthoritiesConstants;

public class UserGenerator implements Generator<User> {
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
	
	private PasswordEncoder encoder;

	public UserGenerator(List<String> cities, PasswordEncoder encoder) {
		super();
		this.encoder = encoder;
	}

	@Override
	public List<User> generate(int limit) {
		List<Role> allRoles = new ArrayList<>();
    	allRoles.add(Role.ROLE_USER);
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
    		}
    	}
    	return users;
	}

	
	private String getEncodedPassword(String userPassword) {
		return this.encoder.encode(userPassword);
	}
}
