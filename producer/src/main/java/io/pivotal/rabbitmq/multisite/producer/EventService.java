package io.pivotal.rabbitmq.multisite.producer;

public interface EventService {
	void notifyEvent(Event event);
}
