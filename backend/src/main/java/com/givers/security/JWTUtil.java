//package com.givers.security;
//
//import static io.jsonwebtoken.SignatureAlgorithm.HS512;
//
//import java.io.Serializable;
//import java.util.Base64;
//import java.util.Date;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//@Component
//public class JWTUtil implements Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	
//	@Value("${springbootwebfluxjjwt.jjwt.secret}")
//	private String secret;
//	
//	@Value("${springbootwebfluxjjwt.jjwt.expiration}")
//	private String expirationTime;
//	
//	public Claims getAllClaimsForToken(String token) {
//		return Jwts.
//				parser()
//				.setSigningKey(
//						Base64
//							.getEncoder()
//							.encodeToString(
//									secret.getBytes()
//							))
//				.parseClaimsJws(token)
//				.getBody();
//	}
//	
//	public String getUsernameFromToken(String token) {
//		return getAllClaimsForToken(token).getSubject();
//	}
//	
//	public Date getExpirationDateFromToken(String token) {
//		return getAllClaimsForToken(token).getExpiration();
//	}
//	
//	private Boolean isTokenExpired(String token) {
//		final Date expirationDate = getExpirationDateFromToken(token);
//		return expirationDate.before(new Date());
//	}
//	
////	public String generateToken(User user) {
////		Map<String, Object> claims = new HashMap<>();
////		claims.put("role", user.getRoles());
////		return doGenerateToken(claims, user.getUsername());
////	}
//
//	private String doGenerateToken(Map<String, Object> claims, String username) {
//		Long expirationTimeLong = Long.parseLong(expirationTime); //in second
//		final Date createdDate = new Date();
//		final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);
//		return Jwts
//				.builder()
//				.setClaims(claims)
//				.setIssuedAt(createdDate)
//				.setExpiration(expirationDate)
//				.setSubject(username)
//				.signWith(HS512, secret.getBytes())
//				.compact();
//	}
//	
//	public Boolean validateToken(String token) {
//		return !this.isTokenExpired(token);
//	}
//}
