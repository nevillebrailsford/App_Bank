package applications.bank.model;

import java.time.LocalDate;
import java.util.UUID;

import application.model.Money;

public class Transfer {
	private Transaction from = null;
	private Transaction to = null;
	private Money amount;
	private LocalDate date;
	private String description;

	public Transfer(Account from, Account to, Money amount, LocalDate date, String description) {
		UUID transactionId = UUID.randomUUID();
		String desc = description == null || description.isEmpty() ? "" : "( " + description + " )";
		String fromDescription = "Transfer to " + to.toString() + desc;
		String toDescription = "Transfer from " + from.toString() + desc;
		this.amount = amount;
		this.date = date;
		this.description = description;
		this.from = new Transaction.Builder().account(from).amount(amount.negate()).date(date)
				.description(fromDescription).transactionId(transactionId.toString()).build();
		this.to = new Transaction.Builder().account(to).amount(amount).date(date).description(toDescription)
				.transactionId(transactionId.toString()).build();
	}

	public static Transfer reverse(Transfer transfer) {
		Transfer reverse = new Transfer(transfer.to.owner(), transfer.from.owner(), transfer.amount, transfer.date(),
				transfer.description());
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

	public String description() {
		return description;
	}
}
