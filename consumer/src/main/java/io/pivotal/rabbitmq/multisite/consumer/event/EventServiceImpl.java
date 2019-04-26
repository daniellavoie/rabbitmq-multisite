package io.pivotal.rabbitmq.multisite.consumer.event;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EventServiceImpl implements EventService {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

	private EventRepository eventRepository;
	private String sourceToProcess;
	private Map<String, EventReplicationProcessor<?>> eventReplicationProcessors;

	private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

	public EventServiceImpl(EventRepository eventRepository,
			List<EventReplicationProcessor<?>> eventReplicationProcessors,
			@Value("${consumer.source:}") String sourceToProcess) {
		this.eventRepository = eventRepository;
		this.eventReplicationProcessors = eventReplicationProcessors.stream()
				.collect(Collectors.toMap(processor -> processor.getSupportedType().getName(), processor -> processor));
		this.sourceToProcess = sourceToProcess;
	}

	@Override
	public void deleteAll() {
		eventRepository.deleteAll();
	}

	public void processEvent(Event event) {
		EventReplicationProcessor<?> processor = Optional
				.ofNullable(eventReplicationProcessors.get(event.getMessageClass()))
				.orElseThrow(() -> new RuntimeException(
						"Could not find any event replication processor for " + event.getMessageClass() + "."));

		processor.convertAndProcessEvent(event);
	}

	@StreamListener(EventSink.INPUT)
	public void processEventNotification(Event event) {
		eventRepository.save(event);

		if ("".equals(sourceToProcess) || event.getSource().equals(sourceToProcess)) {
			processEvent(event);
		}
	}

	@Override
	@Transactional
	public void recoverEvents(String source, long fromEventNumber) {
		LOGGER.info("Recovering event from " + source + " generated after " + fromEventNumber + ".");

		try (Stream<Event> stream = eventRepository.findBySourceAndEventNumberGreaterThanEqual(source,
				fromEventNumber)) {
			stream.forEach(this::processEvent);
		}

		LOGGER.info("Event recovery completed.ﬂØæ");
	}

	public interface EventSink {
		String INPUT = "event";

		@Input(EventSink.INPUT)
		SubscribableChannel input();
	}
}
