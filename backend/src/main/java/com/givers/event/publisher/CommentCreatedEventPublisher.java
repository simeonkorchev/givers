package com.givers.event.publisher;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.givers.event.CommentCreatedEvent;

import reactor.core.publisher.FluxSink;

@Component
public class CommentCreatedEventPublisher implements
    ApplicationListener<CommentCreatedEvent>, 
    Consumer<FluxSink<CommentCreatedEvent>> { 

    private final Executor executor;
    private final BlockingQueue<CommentCreatedEvent> queue =
        new LinkedBlockingQueue<>(); 

    CommentCreatedEventPublisher(Executor executor) {
        this.executor = executor;
    }

    
    @Override
    public void onApplicationEvent(CommentCreatedEvent event) {
        this.queue.offer(event);
    }

     @Override
    public void accept(FluxSink<CommentCreatedEvent> sink) {
        this.executor.execute(() -> {
            while (true)
                try {
                	CommentCreatedEvent event = queue.take(); 
                    sink.next(event); 
                }
                catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }
}