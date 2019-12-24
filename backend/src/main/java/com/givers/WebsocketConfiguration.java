package com.givers;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.givers.event.CommentCreatedEvent;
import com.givers.event.publisher.CommentCreatedEventPublisher;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2
@Configuration
class WebSocketConfiguration {

    // <1>
    @Bean
    Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    // <2>
    @Bean
    HandlerMapping handlerMapping(WebSocketHandler wsh) {
        return new SimpleUrlHandlerMapping() {
            {
                log.info("Setting url");
                setUrlMap(Collections.singletonMap("/ws/comments", wsh));
                setOrder(10);
            }
        };
    }

    // <4>
    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(
        ObjectMapper objectMapper,
        CommentCreatedEventPublisher eventPublisher
    ) {

        Flux<CommentCreatedEvent> publish = Flux
            .create(eventPublisher)
            .share();

        return session -> {

            Flux<WebSocketMessage> messageFlux = publish
                .map(evt -> {
                    try {
                        return objectMapper.writeValueAsString(evt.getSource());
                    }
                    catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(str -> {
                    log.info("Sending " + str);
                    return session.textMessage(str);
                });

            return session.send(messageFlux);
        };
    }

}