package applications.bank.storage;

import application.audit.AuditType;

public enum BranchAuditType implements AuditType {
	Added("added"), Changed("changed"), Removed("removed");

	private String type;

	BranchAuditType(String type) {
		this.type = type;
	}

	@Override
	public String type() {
		return type;
	}

}
