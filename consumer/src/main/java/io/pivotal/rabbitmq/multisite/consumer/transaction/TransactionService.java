package io.pivotal.rabbitmq.multisite.consumer.transaction;

public interface TransactionService {
	long count();
	
	void deleteAll();
	
	Transaction save(Transaction transaction);
}
