package io.pivotal.rabbitmq.multisite.consumer.event;

public interface EventService {
	void deleteAll();
	
	void recoverEvents(String source, long fromEventNumber);
}
