package com.givers.repository.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.givers.validators.ValidEmail;
import com.givers.validators.ValidPassword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document 
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
    private String id;
	
	@ValidEmail
    private String email;
 
    private String username;
    private String firstName;
    private String lastName;
    
    @ValidPassword
    private String password;
    private List<String> causes;
    private List<String> commentIds;
    
//    @Getter @Setter
//	private List<Role> roles;
    private String photoPath;
    private int honor;
    
    private List<Authority> authorities;
	
//	@JsonIgnore
//	@Override
//	public String getPassword() {
//		return password;
//	}
//	
//	@JsonProperty
//	public void setPassword(String password) {
//		this.password = password;
//	}
}


