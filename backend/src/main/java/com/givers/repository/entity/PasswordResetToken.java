package com.givers.repository.entity;

import java.util.Calendar;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document
@NoArgsConstructor
@Data
@Getter
public class PasswordResetToken {
    private static final int EXPIRATION_MINUTES = 60 * 24;

	@Id
	private String id;
	
	private String token;
	private User user;
	private Date expiryDate;
	
	public PasswordResetToken(final String token, final User user) {
        super();

        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION_MINUTES);
    }

	private static Date calculateExpiryDate(int expiryTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(new Date().getTime());
		cal.add(Calendar.MINUTE, expiryTime);
		return new Date(cal.getTime().getTime());
	}

	
}
