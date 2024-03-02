package applications.bank.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.model.ElementBuilder;
import application.model.ElementChecker;
import application.model.Money;

public class Account implements Comparable<Account> {

	private List<Transaction> transactions = new ArrayList<>();
	private List<StandingOrder> standingOrders = new ArrayList<>();
	private AccountId accountId = null;
	private AccountType accountType = null;
	private Branch owner = null;

	Account(AccountType accountType, AccountId accountId, Branch branch) {
		if (accountType == null) {
			throw new IllegalArgumentException("Account: accountType is null");
		}
		if (accountId == null) {
			throw new IllegalArgumentException("Account: accountId is null");
		}
		if (branch == null) {
			throw new IllegalArgumentException("Account: branch is null");
		}
		this.accountType = accountType;
		this.accountId = accountId;
		this.owner = branch;
	}

	public Account(Account that) {
		if (that == null) {
			throw new IllegalArgumentException("Account: account is null");
		}
		this.accountType = that.accountType;
		this.accountId = that.accountId;
		this.owner = that.owner;
		for (Transaction transaction : that.transactions) {
			transactions.add(transaction);
		}
		for (StandingOrder standingOrder : that.standingOrders) {
			standingOrders.add(standingOrder);
		}
	}

	public Account(Element accountElement) {
		this(accountElement, XMLConstants.ACCOUNT);
	}

	public Account(Element accountElement, String elementName) {
		if (accountElement == null) {
			throw new IllegalArgumentException("Account: accountElement is null");
		}
		if (!ElementChecker.verifyTag(accountElement, elementName)) {
			throw new IllegalArgumentException("Account: accountElement is not for account");
		}
		String type = accountElement.getElementsByTagName(XMLConstants.ACCOUNTTYPE).item(0).getTextContent();
		String holder = accountElement.getElementsByTagName(XMLConstants.ACCOUNTHOLDER).item(0).getTextContent();
		String number = accountElement.getElementsByTagName(XMLConstants.ACCOUNTNUMBER).item(0).getTextContent();
		accountType = AccountType.valueOf(type);
		accountId = new AccountId(holder, number);
	}

	public Element buildElement(Document document) {
		return buildElement(document, XMLConstants.ACCOUNT);
	}

	public Element buildElement(Document document, String elementName) {
		if (document == null) {
			throw new IllegalArgumentException("Account: document is null");
		}
		Element result = document.createElement(elementName);
		result.appendChild(ElementBuilder.build(XMLConstants.ACCOUNTTYPE, accountType.toString(), document));
		result.appendChild(ElementBuilder.build(XMLConstants.ACCOUNTHOLDER, accountId.accountHolder(), document));
		result.appendChild(ElementBuilder.build(XMLConstants.ACCOUNTNUMBER, accountId.accountNumber(), document));
		return result;
	}

	public void clear() {
		transactions.clear();
		standingOrders.clear();
	}

	public Branch owner() {
		return owner;
	}

	public AccountId accountId() {
		return accountId;
	}

	public AccountType accountType() {
		return accountType;
	}

	public void setOwner(Branch owner) {
		this.owner = owner;
	}

	public void addTransaction(Transaction transaction) {
		if (transaction == null) {
			throw new IllegalArgumentException("Account: transaction is null");
		}
		transactions.add(transaction);
	}

	public void removeTransaction(Transaction transaction) {
		if (transaction == null) {
			throw new IllegalArgumentException("Account: transaction is null");
		}
		if (!transactions.contains(transaction)) {
			throw new IllegalArgumentException("Account: transaction " + transaction + " not found");
		}
		int found = -1;
		for (int index = 0; index < transactions.size(); index++) {
			if (transactions.get(index).equals(transaction)) {
				found = index;
				break;
			}
		}
		if (found >= 0) {
			transactions.remove(transaction);
		} else {
			throw new IllegalArgumentException("Bank: transaction " + transaction + " not found");
		}
	}

	public void addStandingOrder(StandingOrder standingOrder) {
		if (standingOrder == null) {
			throw new IllegalArgumentException("Account: standingOrder is null");
		}
		standingOrders.add(standingOrder);
	}

	public void updateStandingOrder(StandingOrder standingOrder) {
		if (standingOrder == null) {
			throw new IllegalArgumentException("Account: standingOrder is null");
		}
		if (!standingOrders.contains(standingOrder)) {
			throw new IllegalArgumentException("Account: standingOrder " + standingOrder + " not found");
		}
		int found = -1;
		for (int index = 0; index < standingOrders.size(); index++) {
			if (standingOrders.get(index).equals(standingOrder)) {
				found = index;
				break;
			}
		}
		if (found >= 0) {
			standingOrders.set(found, standingOrder);
		} else {
			throw new IllegalArgumentException("Bank: standingOrder " + standingOrder + " not found");
		}
	}

	public void removeStandingOrder(StandingOrder standingOrder) {
		if (standingOrder == null) {
			throw new IllegalArgumentException("Account: standingOrder is null");
		}
		if (!standingOrders.contains(standingOrder)) {
			throw new IllegalArgumentException("Account: standingOrder " + standingOrder + " not found");
		}
		int found = -1;
		for (int index = 0; index < standingOrders.size(); index++) {
			if (standingOrders.get(index).equals(standingOrder)) {
				found = index;
				break;
			}
		}
		if (found >= 0) {
			standingOrders.remove(standingOrder);
		} else {
			throw new IllegalArgumentException("Bank: standingOrder " + standingOrder + " not found");
		}
	}

	public Money balance() {
		return balance(LocalDate.now());
	}

	public Money balance(LocalDate onDate) {
		List<Money> monies = new ArrayList<>();
		for (Transaction transaction : transactions()) {
			if (!transaction.date().isAfter(onDate)) {
				monies.add(transaction.amount());
			}
		}
		return Money.sum(monies);
	}

	public List<Transaction> transactions() {
		List<Transaction> copyList = transactions.stream().sorted().collect(Collectors.toList());
		return copyList;
	}

	public List<StandingOrder> standingOrders() {
		List<StandingOrder> copyList = standingOrders.stream().sorted().collect(Collectors.toList());
		return copyList;
	}

	public StandingOrder locateStandingOrder(StandingOrder standingOrder) {
		if (standingOrder == null) {
			throw new IllegalArgumentException("Account: standingOrder is null");
		}
		StandingOrder found = null;
		for (StandingOrder so : standingOrders) {
			if (so.equals(standingOrder)) {
				found = so;
				break;
			}
		}
		return found;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId.accountNumber(), accountType, owner);
	}

	@Override
	public int compareTo(Account that) {
		if (this == that)
			return 0;
		int retCode = this.owner.sortCode().compareTo(that.owner.sortCode());
		if (retCode == 0) {
			retCode = this.accountId.compareTo(that.accountId);
			if (retCode == 0) {
				retCode = this.accountType.compareTo(that.accountType);
			}
		}
		return retCode;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (getClass() != that.getClass())
			return false;
		Account other = (Account) that;
		return Objects.equals(accountId.accountNumber(), other.accountId.accountNumber())
				&& Objects.equals(accountType, other.accountType) && Objects.equals(owner, other.owner);
	}

	@Override
	public String toString() {
		return accountId.accountNumber();
	}

	public String toFullString() {
		return "Account named " + accountId.accountHolder() + ", account numbered " + accountId.accountNumber();
	}

	public static class Builder {

		private String accountholder;
		private String accountnumber;
		private AccountType accountType;
		private AccountId accountId = null;
		private Branch branch;

		public Builder accountType(AccountType accountType) {
			this.accountType = accountType;
			return this;
		}

		public Builder accountHolder(String holder) {
			this.accountholder = holder;
			return this;
		}

		public Builder accountNumber(String number) {
			this.accountnumber = number;
			return this;
		}

		public Builder branch(Branch branch) {
			this.branch = branch;
			return this;
		}

		public Account build() {
			if (accountType == null) {
				throw new IllegalArgumentException("Account.Builder: accountType is null");
			}
			if (branch == null) {
				throw new IllegalArgumentException("Account.Builder: branch is null");
			}
			accountId = new AccountId(accountholder, accountnumber);
			return new Account(accountType, accountId, branch);
		}
	}
}
