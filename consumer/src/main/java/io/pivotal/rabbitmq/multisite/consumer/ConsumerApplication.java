package io.pivotal.rabbitmq.multisite.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

import io.pivotal.rabbitmq.multisite.consumer.event.EventServiceImpl.EventSink;

@SpringBootApplication
@EnableBinding(EventSink.class)
public class ConsumerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}
}
