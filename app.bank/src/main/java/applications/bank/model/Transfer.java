package applications.bank.model;

import java.time.LocalDate;
import java.util.UUID;

import application.model.Money;

public class Transfer {
	private Transaction from = null;
	private Transaction to = null;
	private Money amount;

	public Transfer(Account from, Account to, Money amount) {
		UUID transactionId = UUID.randomUUID();
		String fromDescription = "Transfer to " + to.toString();
		String toDescription = "Transfer from " + from.toString();
		this.amount = amount;
		this.from = new Transaction.Builder().account(from).amount(amount.negate()).date(LocalDate.now())
				.description(fromDescription).transactionId(transactionId.toString()).build();
		this.to = new Transaction.Builder().account(to).amount(amount).date(LocalDate.now()).description(toDescription)
				.transactionId(transactionId.toString()).build();
	}

	public static Transfer reverse(Transfer transfer) {
		Transfer reverse = new Transfer(transfer.to.owner(), transfer.from.owner(), transfer.amount);
		return reverse;
	}

	public Transaction from() {
		return from;
	}

	public Transaction to() {
		return to;
	}
}
