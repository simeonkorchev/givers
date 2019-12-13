//package com.givers.converters;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.core.convert.converter.Converter;
//
//import com.givers.repository.entities.Authority;
//import com.givers.repository.entities.User;
//
//import io.r2dbc.spi.Row;
//
//public class UserReadConverter implements Converter<Row, User> {
//
//	@Override
//	public User convert(Row source) {
//		@SuppressWarnings("unchecked")
//		List<String> authoritiesString = (List<String>)source.get("authorities");
//		List<Authority> authorities = new ArrayList<Authority>();
//		for(String authority : authoritiesString) {
//			authorities.add(new Authority(authority));
//		}
//		
//		@SuppressWarnings("unchecked")
//		List<String> causesList = (List<String>)source.get("causes", List.class);
//		User u = new User(source.get("id", String.class), source.get("email", String.class), 
//				source.get("username", String.class), source.get("firstName", String.class),
//				source.get("lastName", String.class), source.get("password", String.class),
//				causesList, source.get("photoPath", String.class),
//				source.get("honor", int.class), authorities);
//		return u;
//	}
//
//}
