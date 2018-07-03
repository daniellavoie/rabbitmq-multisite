package io.pivotal.rabbitmq.multisite.consumer.transaction;

import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {
	private TransactionRepository transactionRepository;

	public TransactionServiceImpl(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@Override
	public Transaction save(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

}
