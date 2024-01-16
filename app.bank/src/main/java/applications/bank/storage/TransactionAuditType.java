package applications.bank.storage;

import application.audit.AuditType;

public enum TransactionAuditType implements AuditType {
	Added("added"), Removed("Removed");

	private String type;

	TransactionAuditType(String type) {
		this.type = type;
	}

	@Override
	public String type() {
		return type;
	}

}
