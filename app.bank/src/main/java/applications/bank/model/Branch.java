package applications.bank.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.model.Address;
import application.model.ElementChecker;
import application.model.SortCode;

public class Branch implements Comparable<Branch> {
	private Address address = null;
	private SortCode sortCode = null;
	private Bank owner = null;

	private List<Account> accounts = new ArrayList<>();

	public Branch(Address address, SortCode sortCode, Bank bank) {
		if (address == null) {
			throw new IllegalArgumentException("Branch: address is null");
		}
		if (sortCode == null) {
			throw new IllegalArgumentException("Branch: sortCode is null");
		}
		if (bank == null) {
			throw new IllegalArgumentException("Branch: bank is null");
		}
		this.address = address;
		this.sortCode = sortCode;
		this.owner = bank;
	}

	public Branch(Branch that) {
		if (that == null) {
			throw new IllegalArgumentException("Branch: branch is null");
		}
		this.sortCode = that.sortCode;
		this.address = that.address;
		this.owner = that.owner;
		for (Account account : that.accounts) {
			this.accounts.add(account);
		}
	}

	public Branch(Element branchElement) {
		this(branchElement, XMLConstants.BRANCH);
	}

	public Branch(Element branchElement, String tagName) {
		if (branchElement == null) {
			throw new IllegalArgumentException("Branch: branchElement is null");
		}
		if (!ElementChecker.verifyTag(branchElement, tagName)) {
			throw new IllegalArgumentException("Branch: branchElement is not for branch");
		}
		this.address = new Address((Element) branchElement.getElementsByTagName(XMLConstants.ADDRESS).item(0));
		this.sortCode = new SortCode((Element) branchElement.getElementsByTagName(XMLConstants.SORTCODE).item(0));
	}

	public Element buildElement(Document document) {
		return buildElement(document, XMLConstants.BRANCH);
	}

	public Element buildElement(Document document, String tagName) {
		if (document == null) {
			throw new IllegalArgumentException("Branch: document is null");
		}
		Element result = document.createElement(tagName);
		result.appendChild(address().buildElement(document));
		result.appendChild(sortCode().buildElement(document));
		return result;
	}

	public Address address() {
		return address;
	}

	public SortCode sortCode() {
		return sortCode;
	}

	public Bank owner() {
		return owner;
	}

	public void setOwner(Bank owner) {
		this.owner = owner;
	}

	public void addAccount(Account account) {
		if (account == null) {
			throw new IllegalArgumentException("Branch: account is null");
		}
		if (accounts.contains(account)) {
			throw new IllegalArgumentException("Branch: account " + account + " already exists");
		}
		accounts.add(account);
	}

	public void replaceAccount(Account account) {
		if (account == null) {
			throw new IllegalArgumentException("Branch: account is null");
		}
		if (!accounts.contains(account)) {
			throw new IllegalArgumentException("Branch: account " + account + " not found");
		}
		int found = -1;
		for (int index = 0; index < accounts.size(); index++) {
			if (accounts.get(index).equals(account)) {
				found = index;
				break;
			}
		}
		if (found >= 0) {
			accounts.set(found, account);
		} else {
			throw new IllegalArgumentException("Bank: account " + account + " not found");
		}
	}

	public void removeAccount(Account account) {
		if (account == null) {
			throw new IllegalArgumentException("Branch: account is null");
		}
		if (!accounts.contains(account)) {
			throw new IllegalArgumentException("Branch: account " + account + " not found");
		}
		int found = -1;
		for (int index = 0; index < accounts.size(); index++) {
			if (accounts.get(index).equals(account)) {
				found = index;
				break;
			}
		}
		if (found >= 0) {
			accounts.remove(account);
		} else {
			throw new IllegalArgumentException("Bank: account " + account + " not found");
		}
	}

	public void clear() {
		accounts.clear();
	}

	public List<Account> accounts() {
		List<Account> copyList = accounts.stream().sorted().collect(Collectors.toList());
		return copyList;
	}

	public Account locateAccount(Account account) {
		if (account == null) {
			throw new IllegalArgumentException("Branch: account is null");
		}
		Account found = null;
		for (Account ac : accounts) {
			if (ac.equals(account)) {
				found = ac;
				break;
			}
		}
		return found;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, sortCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Branch that = (Branch) obj;
		return Objects.equals(address, that.address) && Objects.equals(sortCode, that.sortCode);
	}

	@Override
	public String toString() {
		return sortCode.toString();
	}

	public String toFullString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Branch at ").append(address.toString());
		builder.append(" with sort code ").append(sortCode.toString());
		return builder.toString();
	}

	@Override
	public int compareTo(Branch that) {
		int retCode = this.sortCode.compareTo(that.sortCode);
		if (retCode == 0) {
			retCode = this.address.toString().compareTo(that.address.toString());
		}
		return retCode;
	}

	public static class Builder {

		private Address address;
		private SortCode sortCode;
		private Bank bank;

		public Builder address(Address address) {
			this.address = address;
			return this;
		}

		public Builder sortCode(SortCode sortCode) {
			this.sortCode = sortCode;
			return this;
		}

		public Builder bank(Bank bank) {
			this.bank = bank;
			return this;
		}

		public Branch build() {
			if (address == null) {
				throw new IllegalArgumentException("Branch.Builder: address is null");
			}
			if (sortCode == null) {
				throw new IllegalArgumentException("Branch.Builder: sortCode is null");
			}
			if (bank == null) {
				throw new IllegalArgumentException("Branch.Builder: bank is null");
			}
			return new Branch(address, sortCode, bank);
		}
	}
}
