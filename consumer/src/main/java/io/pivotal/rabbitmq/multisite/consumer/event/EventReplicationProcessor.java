package io.pivotal.rabbitmq.multisite.consumer.event;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class EventReplicationProcessor<T> {
	private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

	public abstract Class<T> getSupportedType();

	public abstract void processEvent(T payload);

	public void convertAndProcessEvent(Event event) {
		try {
			processEvent(objectMapper.readValue(event.getMessage(), getSupportedType()));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
