package io.pivotal.rabbitmq.multisite.consumer.transaction;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Transaction {
	@Id
	private String id;
	private LocalDateTime timestamp;
	private String source;
	private String message;

	public Transaction() {

	}

	public Transaction(String id, LocalDateTime timestamp, String source, String message) {
		this.id = id;
		this.timestamp = timestamp;
		this.source = source;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
