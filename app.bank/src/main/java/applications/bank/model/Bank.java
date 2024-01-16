package applications.bank.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.model.ElementBuilder;
import application.model.ElementChecker;
import application.model.Money;

public class Bank implements Comparable<Bank> {
	private List<Branch> branches = new ArrayList<>();
	private String name = "";

	public Bank(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Bank: name is null");
		}
		if (name.isBlank()) {
			throw new IllegalArgumentException("Bank: name is missing");
		}
		this.name = name;
	}

	public Bank(Bank that) {
		if (that == null) {
			throw new IllegalArgumentException("Bank: bank is null");
		}
		this.name = that.name;
		for (Branch branch : that.branches) {
			Branch newBranch = branch;
			newBranch.setOwner(this);
			this.branches.add(newBranch);
		}
	}

	public Bank(Element bankElement) {
		this(bankElement, XMLConstants.BANK);
	}

	public Bank(Element bankElement, String tagName) {
		if (bankElement == null) {
			throw new IllegalArgumentException("Bank: bankElement is null");
		}
		if (!ElementChecker.verifyTag(bankElement, tagName)) {
			throw new IllegalArgumentException("Bank: bankElement is not for bank");
		}
		String name = bankElement.getElementsByTagName(XMLConstants.BANKNAME).item(0).getTextContent();
		this.name = name;
	}

	public Element buildElement(Document document) {
		return buildElement(document, XMLConstants.BANK);
	}

	public Element buildElement(Document document, String tagName) {
		if (document == null) {
			throw new IllegalArgumentException("Bank: document is null");
		}
		Element result = document.createElement(tagName);
		result.appendChild(ElementBuilder.build(XMLConstants.BANKNAME, name, document));
		return result;
	}

	public String name() {
		return name;
	}

	public Money balance() {
		Money balance = new Money("0.00");
		for (Branch branch : branches) {
			balance = balance.plus(branch.balance());
		}
		return balance;
	}

	public void addBranch(Branch branch) {
		if (branch == null) {
			throw new IllegalArgumentException("Bank: branch is null");
		}
		if (branches.contains(branch)) {
			throw new IllegalArgumentException("Bank: branch " + branch + " already exists");
		}
		branches.add(branch);
	}

	public void replaceBranch(Branch branch) {
		if (branch == null) {
			throw new IllegalArgumentException("Bank: branch is null");
		}
		if (!branches.contains(branch)) {
			throw new IllegalArgumentException("Bank: branch " + branch + " not found");
		}
		int found = -1;
		for (int index = 0; index < branches.size(); index++) {
			if (branches.get(index).equals(branch)) {
				found = index;
				break;
			}
		}
		if (found >= 0) {
			branches.set(found, branch);
		} else {
			throw new IllegalArgumentException("Bank: branch " + branch + " not found");
		}
	}

	public void removeBranch(Branch branch) {
		if (branch == null) {
			throw new IllegalArgumentException("Bank: branch is null");
		}
		if (!branches.contains(branch)) {
			throw new IllegalArgumentException("Bank: branch " + branch + " not found");
		}
		int found = -1;
		for (int index = 0; index < branches.size(); index++) {
			if (branches.get(index).equals(branch)) {
				found = index;
				break;
			}
		}
		if (found >= 0) {
			branches.remove(found);
		} else {
			throw new IllegalArgumentException("Bank: branch " + branch + " not found");
		}
	}

	public List<Branch> branches() {
		List<Branch> copyList = branches.stream().sorted().collect(Collectors.toList());
		return copyList;
	}

	public void clear() {
		branches.clear();
	}

	public Branch locateBranch(Branch branch) {
		if (branch == null) {
			throw new IllegalArgumentException("Bank: branch is null");
		}
		Branch found = null;
		for (Branch br : branches) {
			if (br.equals(branch)) {
				found = br;
				break;
			}
		}
		return found;
	}

	@Override
	public int compareTo(Bank that) {
		return name.compareTo(that.name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bank other = (Bank) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return name;
	}

	public static class Builder {
		private String name;

		public Builder() {
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Bank build() {
			if (name == null) {
				throw new IllegalArgumentException("Bank.Builder: name is null");
			}
			if (name.isEmpty()) {
				throw new IllegalArgumentException("Bank.Builder: name is missing");
			}
			return new Bank(name);
		}
	}
}
