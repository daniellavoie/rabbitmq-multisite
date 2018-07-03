package io.pivotal.rabbitmq.multisite.producer;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import io.pivotal.rabbitmq.multisite.producer.EventServiceImpl.EventSource;

@Service
@EnableBinding(EventSource.class)
public class EventServiceImpl implements EventService {
	private EventSource eventSource;

	public EventServiceImpl(EventSource eventSource) {
		this.eventSource = eventSource;
	}

	@Override
	public void notifyEvent(Event event) {
		eventSource.output().send(MessageBuilder.withPayload(event).build());
	}

	public interface EventSource {
		static final String OUTPUT = "event";

		@Output(OUTPUT)
		MessageChannel output();
	}
}
