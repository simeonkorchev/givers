//package com.givers.converters;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.data.r2dbc.mapping.OutboundRow;
//import org.springframework.data.r2dbc.mapping.SettableValue;
//
//import com.givers.repository.entities.Authority;
//import com.givers.repository.entities.User;
//
//public class UserWriteConverter implements Converter<User, OutboundRow> {
//
//	@Override
//	public OutboundRow convert(User source) {
//		if (source.getCauses() == null) {
//			source.setCauses(new ArrayList<String>());
//		}
//		
//		List<String> authoritiesString = new ArrayList<>();
//		
//		for(Authority authority : source.getAuthorities()) {
//			authoritiesString.add(authority.getAuthority());
//		}
//		
//		OutboundRow row = new OutboundRow();
//		row.put("id", SettableValue.from(source.getId()));
//		row.put("username", SettableValue.from(source.getUsername()));
//		row.put("firstName", SettableValue.from(source.getFirstName()));
//		row.put("lastName", SettableValue.from(source.getLastName()));
//		row.put("password", SettableValue.from(source.getPassword()));
//		row.put("causes", SettableValue.from(source.getCauses()));
//		row.put("authorities", SettableValue.from(authoritiesString));
//		row.put("honor", SettableValue.from(source.getHonor()));
//		row.put("photoPath", SettableValue.from(source.getPhotoPath()));
//		row.put("email", SettableValue.from(source.getEmail()));
//		return row;
//	}
//
//}
