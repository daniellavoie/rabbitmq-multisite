package io.pivotal.rabbitmq.multisite.consumer.event;

import java.util.UUID;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;

import io.pivotal.rabbitmq.multisite.consumer.transaction.Transaction;
import io.pivotal.rabbitmq.multisite.consumer.transaction.TransactionService;

@Service
public class EventServiceImpl implements EventService {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

	private EventRepository eventRepository;
	private TransactionService transactionService;
	private String sourceToProcess;

	public EventServiceImpl(EventRepository eventRepository, TransactionService transactionService,
			@Value("${consumer.source:}") String sourceToProcess) {
		this.eventRepository = eventRepository;
		this.transactionService = transactionService;
		this.sourceToProcess = sourceToProcess;
	}

	@Override
	public void deleteAll() {
		eventRepository.deleteAll();
	}

	private void generateTransaction(Event event) {
		transactionService.save(new Transaction(UUID.randomUUID().toString(), event.getTimestamp(), event.getSource(),
				event.getMessage()));
	}

	@StreamListener(EventSink.INPUT)
	public void processEvent(Event event) {
		eventRepository.save(event);

		if ("".equals(sourceToProcess) || event.getSource().equals(sourceToProcess)) {
			generateTransaction(event);
		}
	}

	@Override
	@Transactional
	public void recoverEvents(String source, long fromEventNumber) {
		LOGGER.info("Recovering event from " + source + " generated after " + fromEventNumber + ".");

		try (Stream<Event> stream = eventRepository.findBySourceAndEventNumberGreaterThanEqual(source,
				fromEventNumber)) {
			stream.forEach(this::generateTransaction);
		}

		LOGGER.info("Event recovery completed.ﬂØæ");
	}

	public interface EventSink {
		String INPUT = "event";

		@Input(EventSink.INPUT)
		SubscribableChannel input();
	}
}
