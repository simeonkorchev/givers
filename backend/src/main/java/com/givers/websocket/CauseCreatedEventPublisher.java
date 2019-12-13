package com.givers.websocket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;

import com.givers.event.CauseCreatedEvent;

import reactor.core.publisher.FluxSink;

public class CauseCreatedEventPublisher implements ApplicationListener<CauseCreatedEvent>, Consumer<FluxSink<CauseCreatedEvent>>{
	private final Executor executor;
    private final BlockingQueue<CauseCreatedEvent> queue =
        new LinkedBlockingQueue<>();
    
    public CauseCreatedEventPublisher(Executor e) {
    	this.executor = e;
    }
    
	@Override
	public void accept(FluxSink<CauseCreatedEvent> sink) {
		this.executor.execute(() -> {
			while(true) {
				try {
					CauseCreatedEvent e = queue.take();
					sink.next(e);
				} catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
			}
		});
	}

	@Override
	public void onApplicationEvent(CauseCreatedEvent event) {
		this.queue.offer(event);
	}

}
