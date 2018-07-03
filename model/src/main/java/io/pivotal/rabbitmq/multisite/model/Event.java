package io.pivotal.rabbitmq.multisite.model;

import java.time.LocalDateTime;

public class Event {
	private String id;
	private LocalDateTime timestamp;
	private String source;
	private String message;

	public Event(String id, LocalDateTime timestamp, String source, String message) {
		this.id = id;
		this.timestamp = timestamp;
		this.source = source;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	}
}
