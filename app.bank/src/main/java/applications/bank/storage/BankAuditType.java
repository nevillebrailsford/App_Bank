package applications.bank.storage;

import application.audit.AuditType;

public enum BankAuditType implements AuditType {
	Added("added"), Changed("changed"), Removed("removed");

	private String type;

	BankAuditType(String type) {
		this.type = type;
	}

	@Override
	public String type() {
		return type;
	}

}
