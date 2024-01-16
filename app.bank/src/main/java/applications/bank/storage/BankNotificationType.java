package applications.bank.storage;

import application.notification.NotificationType;

public enum BankNotificationType implements NotificationType {

	Add("add"), Changed("changed"), Removed("removed"), Failed("failed");

	private String type;

	BankNotificationType(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}

	@Override
	public String category() {
		return ModelConstants.BANK_CATEGORY;
	}

}
