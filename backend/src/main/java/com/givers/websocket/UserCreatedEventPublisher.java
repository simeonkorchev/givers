package com.givers.websocket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.givers.event.UserCreatedEvent;

import reactor.core.publisher.FluxSink;

@Component
public class UserCreatedEventPublisher implements ApplicationListener<UserCreatedEvent>, Consumer<FluxSink<UserCreatedEvent>>{
	private final Executor executor;
    private final BlockingQueue<UserCreatedEvent> queue =
        new LinkedBlockingQueue<>();
    
    public UserCreatedEventPublisher(Executor executor) {
    	this.executor = executor;
    }
    
	@Override
	public void accept(FluxSink<UserCreatedEvent> sink) {
			this.executor.execute(() -> {
				while(true) {
					try {
						UserCreatedEvent event = this.queue.take();
						sink.next(event);
					} catch (InterruptedException e) {
	                    ReflectionUtils.rethrowRuntimeException(e);
	                }
				}
			});
	}

	@Override
	public void onApplicationEvent(UserCreatedEvent event) {
		this.queue.offer(event);
	}

}
