package com.givers.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository{
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Override
	public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		log.info("Authentication start");
    	
		ServerHttpRequest request = exchange.getRequest();
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		
		if (authHeader != null && authHeader.startsWith("Bearer " )) {
			String authToken = authHeader.substring(7);
			Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
			return this.authenticationManager
					.authenticate(auth)
					.map(a -> {
						return new SecurityContextImpl(a);	
			});
		}
		return Mono.empty();
	}

}
