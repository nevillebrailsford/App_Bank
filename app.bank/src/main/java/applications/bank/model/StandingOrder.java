package applications.bank.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.model.AppXMLConstants;
import application.model.ElementBuilder;
import application.model.ElementChecker;
import application.model.Money;
import application.model.Period;

public class StandingOrder implements Comparable<StandingOrder> {
	private Account recipient = null;
	private Account owner = null;
	private Period frequency = null;
	private Money amount = null;
	private LocalDate nextActionDue = null;
	private String reference = "";
	private UUID orderId = null;

	StandingOrder(Account owner, Period frequency, Money amount, LocalDate nextActionDue, String reference,
			Account... recipient) {
		if (owner == null) {
			throw new IllegalArgumentException("StandingOrder: owner is null");
		}
		if (frequency == null) {
			throw new IllegalArgumentException("StandingOrder: frequency is null");
		}
		if (amount == null) {
			throw new IllegalArgumentException("StandingOrder: amount is null");
		}
		if (recipient.length > 1) {
			throw new IllegalArgumentException("StandingOrder: too many recipients");
		}
		if (recipient.length == 1 && recipient[0] != null && recipient[0].equals(owner)) {
			throw new IllegalArgumentException("StandingOrder: recipient is the owner");
		}
		if (reference == null || reference.isBlank()) {
			throw new IllegalArgumentException("StandingOrder: reference is null or blank");
		}
		this.owner = new Account(owner);
		this.frequency = frequency;
		this.amount = amount;
		this.reference = reference;
		this.orderId = UUID.randomUUID();
		if (nextActionDue == null) {
			this.nextActionDue = LocalDate.now();
		} else {
			this.nextActionDue = nextActionDue;
		}
		if (recipient.length == 1 && recipient[0] != null) {
			this.recipient = new Account(recipient[0]);
		}
	}

	public StandingOrder(StandingOrder that) {
		if (that == null) {
			throw new IllegalArgumentException("StandingOrder: owner is null");
		}
		this.owner = that.owner;
		this.frequency = that.frequency;
		this.amount = that.amount;
		this.reference = that.reference;
		this.orderId = that.orderId;
		this.nextActionDue = that.nextActionDue;
		if (that.recipient != null) {
			this.recipient = that.recipient;
		}
	}

	public StandingOrder(Element standingOrderElement) {
		if (standingOrderElement == null) {
			throw new IllegalArgumentException("StandingOrder: standingOrderElement is null");
		}
		if (!ElementChecker.verifyTag(standingOrderElement, XMLConstants.STANDING_ORDER)) {
			throw new IllegalArgumentException("StandingOrder: standingOrderElement is not for owner");
		}
		String amount = standingOrderElement.getElementsByTagName(AppXMLConstants.MONEY).item(0).getTextContent();
		String frequency = standingOrderElement.getElementsByTagName(XMLConstants.FREQUENCY).item(0).getTextContent();
		String nextAction = standingOrderElement.getElementsByTagName(XMLConstants.NEXT_ACTION_DUE).item(0)
				.getTextContent();
		String description = standingOrderElement.getElementsByTagName(XMLConstants.REFERENCE).item(0).getTextContent();
		String orderId = standingOrderElement.getElementsByTagName(XMLConstants.ORDER_ID).item(0).getTextContent();
		this.amount = new Money(amount);
		this.frequency = Period.valueOf(frequency);
		this.reference = description;
		this.orderId = UUID.fromString(orderId);
		this.nextActionDue = LocalDate.parse(nextAction);
		if (standingOrderElement.getElementsByTagName(XMLConstants.RECIPIENT_ACCOUNT).getLength() > 0) {
			Element accountElement = (Element) standingOrderElement.getElementsByTagName(XMLConstants.RECIPIENT_ACCOUNT)
					.item(0);
			Account newAccount = new Account(accountElement, XMLConstants.RECIPIENT_ACCOUNT);
			Element branchElement = (Element) standingOrderElement.getElementsByTagName(XMLConstants.RECIPIENT_BRANCH)
					.item(0);
			Branch branch = new Branch(branchElement, XMLConstants.RECIPIENT_BRANCH);
			Element bankElement = (Element) standingOrderElement.getElementsByTagName(XMLConstants.RECIPIENT_BANK)
					.item(0);
			Bank bank = new Bank(bankElement, XMLConstants.RECIPIENT_BANK);
			branch.setOwner(bank);
			newAccount.setOwner(branch);
			this.recipient = newAccount;
		}
	}

	public Element buildElement(Document document) {
		if (document == null) {
			throw new IllegalArgumentException("Branch: document is null");
		}
		Element result = document.createElement(XMLConstants.STANDING_ORDER);
		result.appendChild(amount().buildElement(document));
		result.appendChild(ElementBuilder.build(XMLConstants.FREQUENCY, frequency().toString(), document));
		result.appendChild(ElementBuilder.build(XMLConstants.NEXT_ACTION_DUE, nextActionDue().toString(), document));
		result.appendChild(ElementBuilder.build(XMLConstants.REFERENCE, reference(), document));
		result.appendChild(ElementBuilder.build(XMLConstants.ORDER_ID, orderId().toString(), document));
		if (recipient() != null) {
			result.appendChild(recipient().owner().owner().buildElement(document, XMLConstants.RECIPIENT_BANK));
			result.appendChild(recipient().owner().buildElement(document, XMLConstants.RECIPIENT_BRANCH));
			result.appendChild(recipient().buildElement(document, XMLConstants.RECIPIENT_ACCOUNT));
		}
		return result;
	}

	public Money amount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public Period frequency() {
		return frequency;
	}

	public void setFrequency(Period frequency) {
		this.frequency = frequency;
	}

	public LocalDate nextActionDue() {
		return nextActionDue;
	}

	public Account recipient() {
		return recipient;
	}

	public Account owner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = new Account(owner);
	}

	public String reference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public UUID orderId() {
		return orderId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(owner, amount, recipient);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StandingOrder other = (StandingOrder) obj;
		return Objects.equals(orderId.toString(), other.orderId.toString());
	}

	@Override
	public String toString() {
		return reference;
	}

	@Override
	public int compareTo(StandingOrder that) {
		int retCode = this.owner.compareTo(that.owner);
		if (retCode == 0) {
			if (recipient != null) {
				if (that.recipient != null) {
					retCode = this.recipient.compareTo(that.recipient);
				} else {
					retCode = 1;
				}
			} else {
				if (that.recipient != null) {
					retCode = 1;
				}
			}
		}
		if (retCode == 0) {
			retCode = this.amount.compareTo(that.amount);
		}
		return retCode;
	}

	public void processed() {
		switch (frequency) {
			case WEEKLY -> {
				nextActionDue = nextActionDue.plusDays(7);
			}
			case MONTHLY -> {
				nextActionDue = nextActionDue.plusMonths(1);
			}
			default -> {
				nextActionDue = nextActionDue.plusYears(1);
			}
		}
	}

	public static class Builder {
		private Account recipient = null;
		private Account owner = null;
		private Money amount = null;
		private Period frequency = null;
		private LocalDate nextActionDue = null;
		private String reference = null;

		public Builder() {
		}

		public Builder recipient(Account recipient) {
			this.recipient = recipient;
			return this;
		}

		public Builder amount(String amount) {
			this.amount = new Money(amount);
			return this;
		}

		public Builder amount(Money amount) {
			this.amount = new Money(amount);
			return this;
		}

		public Builder frequency(Period frequency) {
			this.frequency = frequency;
			return this;
		}

		public Builder owner(Account owner) {
			this.owner = owner;
			return this;
		}

		public Builder nextActionDue(LocalDate nextActionDue) {
			this.nextActionDue = nextActionDue;
			return this;
		}

		public Builder reference(String reference) {
			this.reference = reference;
			return this;
		}

		public StandingOrder build() {
			if (amount == null) {
				throw new IllegalArgumentException("StandingOrder.Builder: amount is null");
			}
			if (frequency == null) {
				throw new IllegalArgumentException("StandingOrder.Builder: frequency is null");
			}
			if (owner == null) {
				throw new IllegalArgumentException("StandingOrder.Builder: owner is null");
			}
			if (reference == null) {
				throw new IllegalArgumentException("StandingOrder.Builder: reference is null");
			}
			return new StandingOrder(owner, frequency, amount, nextActionDue, reference, recipient);
		}
	}

}
