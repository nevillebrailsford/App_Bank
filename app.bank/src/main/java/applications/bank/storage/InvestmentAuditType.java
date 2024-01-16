package applications.bank.storage;

import application.audit.AuditType;

public enum InvestmentAuditType implements AuditType {
	Added("added"), Changed("changed"), Removed("removed");

	private String type;

	InvestmentAuditType(String type) {
		this.type = type;
	}

	@Override
	public String type() {
		return type;
	}
}
