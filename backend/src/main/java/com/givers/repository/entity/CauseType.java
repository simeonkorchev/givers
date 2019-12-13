package com.givers.repository.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public enum CauseType {
	CHILDREN,
	ADULTS,
	HOMELESS,
	ANIMALS,
	NATURE;
	
	public static String[] names() {
		int i = 0;
		String[] names = new String[CauseType.values().length];
		for(CauseType causeType : CauseType.values()) {
			names[i] = causeType.name();
		}
		return names;
	}
}
