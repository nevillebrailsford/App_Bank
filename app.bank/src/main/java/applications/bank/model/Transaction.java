package applications.bank.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.model.ElementBuilder;
import application.model.Money;

public class Transaction implements Comparable<Transaction> {

	private LocalDate date = null;
	private Money amount = null;
	private Account owner = null;
	private String description = null;
	private UUID transactionId = null;

	Transaction(LocalDate date, Money amount, Account account, String description, String... transactionId) {
		if (date == null) {
			throw new IllegalArgumentException("Transaction: date is null");
		}
		if (account == null) {
			throw new IllegalArgumentException("Transaction: account is null");
		}
		if (amount == null) {
			throw new IllegalArgumentException("Transaction: amount is null");
		}
		if (description == null || description.isBlank()) {
			throw new IllegalArgumentException("Transaction: description is null or blank");
		}
		if (transactionId.length > 1) {
			throw new IllegalArgumentException("Transaction: more than 1 transactionId");
		}
		this.date = date;
		this.amount = amount;
		this.owner = new Account(account);
		this.description = description;
		if (transactionId.length == 0) {
			this.transactionId = UUID.randomUUID();
		} else {
			this.transactionId = UUID.fromString(transactionId[0]);
		}
	}

	public Transaction(Transaction that) {
		if (that == null) {
			throw new IllegalArgumentException("Transaction: that is null");
		}
		this.date = that.date;
		this.amount = that.amount;
		this.owner = that.owner;
		this.description = that.description;
		this.transactionId = that.transactionId;
	}

	public Transaction(Element transactionElement) {
		if (transactionElement == null) {
			throw new IllegalArgumentException("Transaction: transactionElement is null");
		}
		String date = transactionElement.getElementsByTagName(XMLConstants.DATE).item(0).getTextContent();
		this.description = transactionElement.getElementsByTagName(XMLConstants.DESCRIPTION).item(0).getTextContent();
		this.date = LocalDate.parse(date);
		this.amount = new Money((Element) transactionElement.getElementsByTagName(XMLConstants.MONEY).item(0));
		this.transactionId = UUID.fromString(
				transactionElement.getElementsByTagName(XMLConstants.TRANSACTION_ID).item(0).getTextContent());
	}

	public Element buildElement(Document document) {
		if (document == null) {
			throw new IllegalArgumentException("Transaction: document is null");
		}
		Element result = document.createElement(XMLConstants.TRANSACTION);
		result.appendChild(ElementBuilder.build(XMLConstants.DATE, date.toString(), document));
		result.appendChild(amount.buildElement(document));
		result.appendChild(ElementBuilder.build(XMLConstants.DESCRIPTION, description, document));
		result.appendChild(ElementBuilder.build(XMLConstants.TRANSACTION_ID, transactionId.toString(), document));
		return result;
	}

	public LocalDate date() {
		return date;
	}

	public Account owner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public Money amount() {
		return amount;
	}

	public String description() {
		return description;
	}

	public UUID transactionId() {
		return transactionId;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (getClass() != that.getClass())
			return false;
		Transaction other = (Transaction) that;
		return Objects.equals(date, other.date) && Objects.equals(owner, other.owner)
				&& Objects.equals(amount, other.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(date, owner, amount);
	}

	@Override
	public int compareTo(Transaction that) {
		return this.date.compareTo(that.date);
	}

	@Override
	public String toString() {
		return "Transaction date=" + date + ", amount=" + amount;
	}

	public static class Builder {
		LocalDate date;
		Money amount;
		Account account;
		String description;
		String transactionId;

		public Builder date(LocalDate date) {
			this.date = date;
			return this;
		}

		public Builder amount(Money amount) {
			this.amount = amount;
			return this;
		}

		public Builder account(Account account) {
			this.account = account;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder transactionId(String transactionId) {
			this.transactionId = transactionId;
			return this;
		}

		public Transaction build() {
			if (date == null) {
				throw new IllegalArgumentException("Transaction.Builder: date is null");
			}
			if (account == null) {
				throw new IllegalArgumentException("Transaction.Builder: account is null");
			}
			if (amount == null) {
				throw new IllegalArgumentException("Transaction.Builder: amount is null");
			}
			if (description == null) {
				throw new IllegalArgumentException("Transaction.Builder: description is null");
			}
			if (transactionId == null) {
				return new Transaction(date, amount, account, description);
			} else {
				return new Transaction(date, amount, account, description, transactionId);
			}
		}
	}
}
