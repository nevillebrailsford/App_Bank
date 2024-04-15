package applications.bank.gui.models;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import application.definition.ApplicationConfiguration;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import applications.bank.model.Account;
import applications.bank.model.Transaction;
import applications.bank.storage.BankMonitor;
import applications.bank.storage.TransactionNotificationType;

public class TransactionsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = TransactionsTableModel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private static String[] COLUMNS = { "Date", "Description", "Transaction Id", "Amount" };
	private static final int DATE = 0;
	private static final int DESCRIPTION = 1;
	private static final int TRANSACTION_ID = 2;
	private static final int AMOUNT = 3;

	private Account account;
	private List<Transaction> transactions;

	private NotificationListener addNotificationListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addNotify");
		Transaction transaction = (Transaction) notification.subject().get();
		if (transaction.owner().equals(account)) {
			SwingUtilities.invokeLater(() -> {
				addTransactionNotify(transaction);
			});
		}
		LOGGER.exiting(CLASS_NAME, "addNotify");
	};

	private NotificationListener removeNotificationListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addNotify");
		Transaction transaction = (Transaction) notification.subject().get();
		if (transaction.owner().equals(account)) {
			SwingUtilities.invokeLater(() -> {
				removeTransactionNotify(transaction);
			});
		}
		LOGGER.exiting(CLASS_NAME, "addNotify");
	};

	public TransactionsTableModel(Account account) {
		this.account = account;
		this.transactions = account.transactions();
		Collections.reverse(transactions);
		addListeners();
	}

	@Override
	public int getRowCount() {
		return transactions.size() + 1;
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
	public Class<?> getColumnClass(int col) {
		if (col == AMOUNT) {
			return Number.class;
		}
		return String.class;
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object value = "Unknown";
		if (row == transactions.size()) {
			value = closingBalance(col);
		} else {
			value = transactionRow(row, col);
		}
		return value;
	}

	private Object closingBalance(int col) {
		Object value = "Unknown";
		switch (col) {
			case DATE:
				value = LocalDate.now().toString();
				break;
			case DESCRIPTION:
				value = "Closing Balance";
				break;
			case TRANSACTION_ID:
				value = "";
				break;
			case AMOUNT:
				value = BankMonitor.instance().balanceAccount(account).cost();
				break;
		}
		return value;
	}

	private Object transactionRow(int row, int col) {
		Object value = "Unknown";
		Transaction transaction = transactions.get(row);
		switch (col) {
			case DATE:
				value = transaction.date().toString();
				break;
			case DESCRIPTION:
				value = transaction.description();
				break;
			case TRANSACTION_ID:
				value = transaction.transactionId().toString();
				break;
			case AMOUNT:
				value = transaction.amount().cost();
				break;
		}
		return value;
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "addListeners");
		NotificationCentre.addListener(addNotificationListener, TransactionNotificationType.Add);
		NotificationCentre.addListener(removeNotificationListener, TransactionNotificationType.Removed);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		NotificationCentre.removeListener(addNotificationListener);
		NotificationCentre.removeListener(removeNotificationListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

	private void addTransactionNotify(Transaction transaction) {
		LOGGER.entering(CLASS_NAME, "addTransactionNotify", transaction);
		int index = findPosition(transaction);
		LOGGER.fine("Initial number of entries in transactions = " + transactions.size());
		if (index == -1) {
			LOGGER.fine("Adding transaction " + transaction.description() + " at end of table");
			transactions.add(transaction);
		} else {
			LOGGER.fine("Adding transaction " + transaction.description() + " at index " + index + " of table");
			transactions.add(index, transaction);
		}
		LOGGER.fine("Final number of entries in transactions = " + transactions.size());
		fireTableDataChanged();
		LOGGER.exiting(CLASS_NAME, "addTransactionNotify");

	}

	public void removeTransactionNotify(Transaction transaction) {
		LOGGER.entering(CLASS_NAME, "removeAccountNotification", transaction);
		int index = findExistingPosition(transaction);
		if (index != -1) {
			LOGGER.fine("Removing transaction " + transaction.description() + " from the table");
			transactions.remove(transaction);
			fireTableDataChanged();
		}
		LOGGER.exiting(CLASS_NAME, "removeTransactionNotify");
	}

	private int findPosition(Transaction transaction) {
		LOGGER.entering(CLASS_NAME, "findPosition", transaction);
		int index = -1;
		for (int i = 0; i < transactions.size(); i++) {
			if (transactions.get(i).compareTo(transaction) > 0) {
				index = i;
				break;
			}
		}
		LOGGER.exiting(CLASS_NAME, "findPosition", index);
		return index;

	}

	private int findExistingPosition(Transaction transaction) {
		LOGGER.entering(CLASS_NAME, "findExistingPosition", transaction);
		int index = -1;
		for (int i = 0; i < transactions.size(); i++) {
			if (transactions.get(i).equals(transaction)) {
				index = i;
				break;
			}
		}
		LOGGER.exiting(CLASS_NAME, "findExistingPosition", index);
		return index;
	}
}
