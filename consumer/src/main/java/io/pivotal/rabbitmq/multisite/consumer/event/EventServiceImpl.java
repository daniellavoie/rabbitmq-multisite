package io.pivotal.rabbitmq.multisite.consumer.event;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;

import io.pivotal.rabbitmq.multisite.consumer.transaction.Transaction;
import io.pivotal.rabbitmq.multisite.consumer.transaction.TransactionService;

@Service
public class EventServiceImpl {
	private EventRepository eventRepository;
	private TransactionService transactionService;
	private String sourceToProcess;

	public EventServiceImpl(EventRepository eventRepository, TransactionService transactionService,
			@Value("${consumer.source-to-process:}") String sourceToProcess) {
		this.eventRepository = eventRepository;
		this.transactionService = transactionService;
		this.sourceToProcess = sourceToProcess;
	}

	@StreamListener(EventSink.INPUT)
	public void processEvent(Event event) {
		eventRepository.save(event);

		if ("".equals(sourceToProcess) || event.getSource().equals(sourceToProcess)) {
			generateTransaction(event);
		}
	}

	private void generateTransaction(Event event) {
		transactionService.save(new Transaction(UUID.randomUUID().toString(), event.getTimestamp(), event.getSource(),
				event.getMessage()));
	}

	public interface EventSink {
		String INPUT = "event";

		@Input(EventSink.INPUT)
		SubscribableChannel input();
	}
}
