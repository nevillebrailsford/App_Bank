package applications.bank.storage;

import application.notification.NotificationType;

public enum BranchNotificationType implements NotificationType {
	Add("add"), Changed("changed"), Removed("removed"), Failed("failed");
	;

	private String type;

	BranchNotificationType(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}

	@Override
	public String category() {
		return ModelConstants.BRANCH_CATEGORY;
	}

}
