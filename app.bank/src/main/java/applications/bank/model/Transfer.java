package applications.bank.model;

import java.time.LocalDate;
import java.util.UUID;

import application.model.Money;

public class Transfer {
	private Transaction from = null;
	private Transaction to = null;
	private Money amount;
	private LocalDate date;

	public Transfer(Account from, Account to, Money amount, LocalDate date) {
		UUID transactionId = UUID.randomUUID();
		String fromDescription = "Transfer to " + to.toString();
		String toDescription = "Transfer from " + from.toString();
		this.amount = amount;
		this.date = date;
		this.from = new Transaction.Builder().account(from).amount(amount.negate()).date(date)
				.description(fromDescription).transactionId(transactionId.toString()).build();
		this.to = new Transaction.Builder().account(to).amount(amount).date(date).description(toDescription)
				.transactionId(transactionId.toString()).build();
	}

	public static Transfer reverse(Transfer transfer) {
		Transfer reverse = new Transfer(transfer.to.owner(), transfer.from.owner(), transfer.amount, transfer.date());
		return reverse;
	}

	public Transaction from() {
		return from;
	}

	public Transaction to() {
		return to;
	}

	public LocalDate date() {
		return date;
	}
}
