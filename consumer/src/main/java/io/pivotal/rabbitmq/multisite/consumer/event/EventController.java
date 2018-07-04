package io.pivotal.rabbitmq.multisite.consumer.event;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {
	private EventService eventService;

	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@DeleteMapping
	public void deleteAll() {
		eventService.deleteAll();
	}

	@GetMapping
	public void recoverEvents(@RequestParam String source, @RequestParam long fromEventNumber) {
		eventService.recoverEvents(source, fromEventNumber);
	}
}
