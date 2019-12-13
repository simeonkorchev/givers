package com.givers.websocket;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.givers.repository.entity.User;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) 
public class WebSocketConfigurationTest {
    private final WebSocketClient socketClient = new ReactorNettyWebSocketClient();
    private final WebClient webClient = WebClient.builder().build();

//    private User generateRandomUser() {
//        return new User(UUID.randomUUID().toString(), UUID.randomUUID().toString() + "@email.com", null, null, null, null, null, null, 0);
//    }
    
    @Test
    public void testNotificationsOnUpdates() throws Exception {
    	int count = 10; 
        AtomicLong counter = new AtomicLong(); 
        URI uri = URI.create("ws://localhost:8080/ws/users");
        
        socketClient.execute(uri, (WebSocketSession s) -> {
        	Mono<WebSocketMessage> out = Mono.just(s.textMessage("test"));
        	Flux<String> in = s
        			.receive()
        			.map(WebSocketMessage::getPayloadAsText);
        	return s
        			.send(out)
        			.thenMany(in)
        			.doOnNext(str -> counter.incrementAndGet())
        			.then();
        }).subscribe();
        
//        Flux
//        	.<User>generate(sink -> sink.next(generateRandomUser()))
//        	.take(count)
//        	.flatMap(this::write)
//        	.blockLast();
    }
    
    private Publisher<User> write(User u) {
    	return this.webClient
			.post()
			.uri("http://localhost:8080/users")
			.body(BodyInserters.fromObject(u))
			.retrieve()
			.bodyToMono(String.class)
			.thenReturn(u);
    }
}
