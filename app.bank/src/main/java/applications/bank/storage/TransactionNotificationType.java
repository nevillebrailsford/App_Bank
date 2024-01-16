package applications.bank.storage;

import application.notification.NotificationType;

public enum TransactionNotificationType implements NotificationType {
	Add("add"), Removed("Removed"), Failed("failed");
	;

	private String type;

	TransactionNotificationType(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}

	@Override
	public String category() {
		return ModelConstants.TRANSACTION_CATEGORY;
	}

}
