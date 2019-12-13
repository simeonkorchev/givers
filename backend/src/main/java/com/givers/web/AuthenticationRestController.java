package com.givers.web;

import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.givers.security.AuthRequest;
import com.givers.security.AuthResponse;
import com.givers.security.AuthenticationManager;
import com.givers.security.TokenProvider;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationRestController {
	private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final Validator validation;

    public AuthenticationRestController(TokenProvider tokenProvider,
                             AuthenticationManager authenticationManager,
                             Validator validation) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.validation = validation;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Mono<AuthResponse> authorize(@Valid @RequestBody AuthRequest request) {
        if (!this.validation.validate(request).isEmpty()) {
            return Mono.error(new RuntimeException("Bad request"));
        }

        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Mono<Authentication> authentication = this.authenticationManager.authenticate(authenticationToken);
        authentication.doOnError(throwable -> {
            throw new BadCredentialsException("Bad crendentials");
        });
        ReactiveSecurityContextHolder.withAuthentication(authenticationToken);

        return authentication.map(auth -> {
            String jwt = tokenProvider.createToken(auth);
            return new AuthResponse(jwt, request.getUsername());
        });
    }
}
