package io.pivotal.rabbitmq.multisite.consumer.event;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, String> {
	Stream<Event> findBySourceAndEventNumberGreaterThanEqual(String source, long eventNumber);
}
