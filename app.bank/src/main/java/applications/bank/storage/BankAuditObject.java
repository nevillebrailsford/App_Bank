package applications.bank.storage;

import application.audit.AuditObject;

public enum BankAuditObject implements AuditObject {
	Bank("bank"), Branch("branch"), Account("account"), Transaction("transaction"), StandingOrder("standingorder"),
	Investment("investment");

	private String object;

	BankAuditObject(String object) {
		this.object = object;
	}

	@Override
	public String object() {
		return object;
	}

}
