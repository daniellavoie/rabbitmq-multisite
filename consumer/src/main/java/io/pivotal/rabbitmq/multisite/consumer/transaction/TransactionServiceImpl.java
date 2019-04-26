package io.pivotal.rabbitmq.multisite.consumer.transaction;

import org.springframework.stereotype.Service;

import io.pivotal.rabbitmq.multisite.consumer.event.EventReplicationProcessor;

@Service
public class TransactionServiceImpl extends EventReplicationProcessor<Transaction> implements TransactionService {
	private TransactionRepository transactionRepository;

	public TransactionServiceImpl(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@Override
	public long count() {
		return transactionRepository.count();
	}

	@Override
	public void deleteAll() {
		transactionRepository.deleteAll();
	}

	@Override
	public Transaction save(Transaction transaction) {
		// Send the transaction to the replication queue.
		this.
		
		return transactionRepository.save(transaction);
	}

	@Override
	public Class<Transaction> getSupportedType() {
		return Transaction.class;
	}

	@Override
	public void processEvent(Transaction payload) {
		save(payload);
	}

}
