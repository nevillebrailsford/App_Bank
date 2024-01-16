package applications.bank.storage;

import application.notification.NotificationType;

public enum StandingOrderNotificationType implements NotificationType {
	Add("add"), Changed("changed"), Removed("Removed"), Failed("failed");

	private String type;

	StandingOrderNotificationType(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}

	@Override
	public String category() {
		return ModelConstants.STANDINGORDER_CATEGORY;
	}

}
