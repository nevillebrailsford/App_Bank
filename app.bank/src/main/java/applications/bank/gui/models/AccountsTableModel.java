package applications.bank.gui.models;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import application.definition.ApplicationConfiguration;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.model.Transaction;
import applications.bank.storage.BankMonitor;
import applications.bank.storage.TransactionNotificationType;

public class AccountsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = AccountsTableModel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();
	private static String[] COLUMNS = { "Sort Code", "Account Number", "Account Holder", "Balance" };

	private static final int SORT_CODE = 0;
	private static final int ACCOUNT_NUMBER = 1;
	private static final int ACCOUNT_HOLDER = 2;
	private static final int BALANCE = 3;

	List<Account> accounts;
	private Bank bank;

	private NotificationListener transactionListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addRemoveNotify", notification);
		Transaction transaction = (Transaction) notification.subject().get();
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "addRemoveNotify");
			refresh(transaction);
			LOGGER.exiting(CLASS_NAME, "addRemoveNotify");
		});
		LOGGER.exiting(CLASS_NAME, "addRemoveNotify");
	};

	public AccountsTableModel(List<Account> accounts, Bank bank) {
		LOGGER.entering(CLASS_NAME, "init", accounts);
		this.bank = bank;
		this.accounts = accounts;
		addListeners();
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	public int getRowCount() {
		LOGGER.entering(CLASS_NAME, "getRowCount");
		int rowCount = accounts.size() + 1;
		LOGGER.exiting(CLASS_NAME, "getRowCount", rowCount);
		return rowCount;
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
		if (col == BALANCE) {
			return Number.class;
		}
		return String.class;
	}

	@Override
	public Object getValueAt(int row, int col) {
		LOGGER.entering(CLASS_NAME, "getValueAt", new Object[] { row, col });
		Object value = "Unknown";
		if (row == accounts.size()) {
			value = totalBalance(col);
		} else {
			value = accountRow(row, col);
		}
		LOGGER.exiting(CLASS_NAME, "getValueAt", value);
		return value;
	}

	private Object totalBalance(int col) {
		LOGGER.entering(CLASS_NAME, "totalBalance", col);
		Object value = "Unknown";
		switch (col) {
			case SORT_CODE:
				value = "";
				break;
			case ACCOUNT_NUMBER:
				value = LocalDate.now().toString();
				break;
			case ACCOUNT_HOLDER:
				value = "Total Balance";
				break;
			case BALANCE:
				value = BankMonitor.instance().balanceBank(bank).cost();
				break;
		}
		LOGGER.exiting(CLASS_NAME, "totalBalance", value);
		return value;
	}

	private Object accountRow(int row, int col) {
		LOGGER.entering(CLASS_NAME, "accountRow");
		Object value = "Unknown";
		Account account = accounts.get(row);
		switch (col) {
			case SORT_CODE:
				value = account.owner().sortCode().toString();
				break;
			case ACCOUNT_NUMBER:
				value = account.accountId().accountNumber();
				break;
			case ACCOUNT_HOLDER:
				value = account.accountId().accountHolder();
				break;
			case BALANCE:
				value = BankMonitor.instance().balanceAccount(account).cost();
				break;
		}
		LOGGER.exiting(CLASS_NAME, "accountRow", value);
		return value;
	}

	public void addAccountNotification(Account account) {
		LOGGER.entering(CLASS_NAME, "addAccountNotification", account);
		int index = findPosition(account);
		if (index == -1) {
			accounts.add(account);
			fireTableRowsInserted(accounts.size(), accounts.size());
		} else {
			accounts.add(index, account);
			fireTableRowsInserted(index, index);
		}
		LOGGER.exiting(CLASS_NAME, "addAccountNotification");
	}

	public void removeAccountNotification(Account account) {
		LOGGER.entering(CLASS_NAME, "removeAccountNotification", account);
		int index = findExistingPosition(account);
		if (index != -1) {
			accounts.remove(account);
			fireTableRowsDeleted(index, index);
		}
		LOGGER.exiting(CLASS_NAME, "removeAccountNotification");
	}

	public Account account(int index) {
		LOGGER.entering(CLASS_NAME, "selectedAccount", index);
		Account account = null;
		if (index >= 0 && index < accounts.size()) {
			account = accounts.get(index);
		}
		LOGGER.exiting(CLASS_NAME, "selectedAccount", account);
		return account;
	}

	private int findPosition(Account account) {
		LOGGER.entering(CLASS_NAME, "findPosition", account);
		int index = -1;
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i).compareTo(account) > 0) {
				index = i;
				break;
			}
		}
		LOGGER.exiting(CLASS_NAME, "findPosition", index);
		return index;
	}

	private int findExistingPosition(Account account) {
		LOGGER.entering(CLASS_NAME, "findExistingPosition", account);
		int index = -1;
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i).equals(account)) {
				index = i;
				break;
			}
		}
		LOGGER.exiting(CLASS_NAME, "findExistingPosition", index);
		return index;
	}

	private void refresh(Transaction transaction) {
		LOGGER.entering(CLASS_NAME, "refresh", transaction);
		int index = findExistingPosition(transaction.owner());
		if (index >= 0) {
			fireTableDataChanged();
		}
		LOGGER.exiting(CLASS_NAME, "refresh");
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "adListeners");
		NotificationCentre.addListener(transactionListener, TransactionNotificationType.Add);
		NotificationCentre.addListener(transactionListener, TransactionNotificationType.Removed);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		NotificationCentre.removeListener(transactionListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}
}
