package io.pivotal.rabbitmq.multisite.consumer.transaction;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	private TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@GetMapping
	public long count() {
		return transactionService.count();
	}

	@DeleteMapping
	public void deleteAll() {
		transactionService.deleteAll();
	}
}
