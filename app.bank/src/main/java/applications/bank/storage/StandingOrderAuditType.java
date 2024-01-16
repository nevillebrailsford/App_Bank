package applications.bank.storage;

import application.audit.AuditType;

public enum StandingOrderAuditType implements AuditType {
	Added("added"), Changed("changed"), Removed("removed");

	private String type;

	StandingOrderAuditType(String type) {
		this.type = type;
	}

	@Override
	public String type() {
		return type;
	}

}
