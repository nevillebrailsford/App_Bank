package applications.bank.gui.models;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import application.definition.ApplicationConfiguration;
import application.model.Money;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import applications.bank.model.Account;
import applications.bank.model.StandingOrder;
import applications.bank.storage.StandingOrderNotificationType;

public class StandingOrdersTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = StandingOrdersTableModel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private static String[] COLUMNS = { "Owner", "Amount", "Frequency", "Next Date", "Recipient", "Reference" };
	private static final int OWNER = 0;
	private static final int AMOUNT = 1;
	private static final int FREQUENCY = 2;
	private static final int NEXT_DATE = 3;
	private static final int RECIPIENT = 4;
	private static final int REFERENCE = 5;

	private Account account;
	private List<StandingOrder> standingOrders;

	private NotificationListener addNotificationListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addNotify");
		StandingOrder standingOrder = (StandingOrder) notification.subject().get();
		if (standingOrder.owner().equals(account)) {
			SwingUtilities.invokeLater(() -> {
				addStandingOrderNotify(standingOrder);
			});
		}
		LOGGER.exiting(CLASS_NAME, "addNotify");
	};

	private NotificationListener removeNotificationListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "removeNotify");
		StandingOrder standingOrder = (StandingOrder) notification.subject().get();
		if (standingOrder.owner().equals(account)) {
			SwingUtilities.invokeLater(() -> {
				removeStandingOrderNotify(standingOrder);
			});
		}
		LOGGER.exiting(CLASS_NAME, "removeNotify");
	};

	public StandingOrdersTableModel(Account account) {
		this.account = account;
		this.standingOrders = account.standingOrdersStream().collect(Collectors.toList());
		addListeners();
	}

	@Override
	public int getRowCount() {
		return standingOrders.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == NEXT_DATE) {
			return LocalDate.class;
		}
		if (columnIndex == AMOUNT) {
			return Money.class;
		}
		return String.class;
	}

	@Override
	public Object getValueAt(int row, int col) {
		StandingOrder standingOrder = standingOrders.get(row);
		Object value = "Unknown";
		switch (col) {
			case OWNER:
				value = standingOrder.owner().accountId().accountNumber();
				break;
			case AMOUNT:
				value = standingOrder.amount();
				break;
			case FREQUENCY:
				value = standingOrder.frequency().toString();
				break;
			case NEXT_DATE:
				value = standingOrder.nextActionDue();
				break;
			case RECIPIENT:
				value = standingOrder.recipient().accountId().accountNumber();
				break;
			case REFERENCE:
				value = standingOrder.reference();
				break;
		}
		return value;
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "addListeners");
		NotificationCentre.addListener(addNotificationListener, StandingOrderNotificationType.Add);
		NotificationCentre.addListener(removeNotificationListener, StandingOrderNotificationType.Removed);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		NotificationCentre.removeListener(addNotificationListener);
		NotificationCentre.removeListener(removeNotificationListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

	private void addStandingOrderNotify(StandingOrder standingOrder) {
		LOGGER.entering(CLASS_NAME, "addStandingOrderNotify", standingOrder);
		int index = findPosition(standingOrder);
		if (index == -1) {
			standingOrders.add(standingOrder);
		} else {
			standingOrders.add(index, standingOrder);
		}
		fireTableDataChanged();
		LOGGER.exiting(CLASS_NAME, "addStandingOrderNotify");

	}

	public void removeStandingOrderNotify(StandingOrder standingOrder) {
		LOGGER.entering(CLASS_NAME, "removeStandingOrderNotify", standingOrder);
		int index = findExistingPosition(standingOrder);
		if (index != -1) {
			standingOrders.remove(standingOrder);
			fireTableDataChanged();
		}
		LOGGER.exiting(CLASS_NAME, "removeStandingOrderNotify");
	}

	private int findPosition(StandingOrder standingOrder) {
		LOGGER.entering(CLASS_NAME, "findPosition", standingOrder);
		int index = -1;
		for (int i = 0; i < standingOrders.size(); i++) {
			if (standingOrders.get(i).compareTo(standingOrder) > 0) {
				index = i;
				break;
			}
		}
		LOGGER.exiting(CLASS_NAME, "findPosition", index);
		return index;
	}

	private int findExistingPosition(StandingOrder standingOrder) {
		LOGGER.entering(CLASS_NAME, "findExistingPosition", standingOrder);
		int index = -1;
		for (int i = 0; i < standingOrders.size(); i++) {
			if (standingOrders.get(i).equals(standingOrder)) {
				index = i;
				break;
			}
		}
		LOGGER.exiting(CLASS_NAME, "findExistingPosition", index);
		return index;
	}
}
