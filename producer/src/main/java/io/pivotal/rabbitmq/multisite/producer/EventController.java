package io.pivotal.rabbitmq.multisite.producer;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {
	private EventService eventService;
	private String source;

	public EventController(EventService eventService, @Value("${producer.source:}") String source) {
		this.eventService = eventService;
		this.source = source;
	}

	@PostMapping
	public void generateEvent(@RequestBody String message, @RequestParam long eventNumber) {
		eventService.notifyEvent(
				new Event(UUID.randomUUID().toString(), LocalDateTime.now(), source, message, eventNumber));
	}
}
