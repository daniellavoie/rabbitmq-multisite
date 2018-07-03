package io.pivotal.rabbitmq.multisite.consumer.transaction;

public interface TransactionService {
	Transaction save(Transaction transaction);
}
