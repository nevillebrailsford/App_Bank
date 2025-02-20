package applications.bank.gui.modified;

import java.awt.BorderLayout;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JPanel;

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import applications.bank.application.IBankApplication;
import applications.bank.gui.actions.BankActionFactory;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.storage.AccountNotificationType;
import applications.bank.storage.TransactionNotificationType;

public class BankPanel extends ColoredPanel {
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = BankPanel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	private Bank bank;
	AccountsPanel accountsPanel;
	private JButton exit;

	private NotificationListener addAccountListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addNotify", notification);
		Account account = (Account) notification.subject().get();
		if (bank.equals(account.owner().owner())) {
			addAccountNotification(account);
			updateMenuItems();
		}
		LOGGER.exiting(CLASS_NAME, "addNotify");
	};

	private NotificationListener changeAccountListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "changeNotify", notification);
		changeAccountNotification();
		updateMenuItems();
		LOGGER.exiting(CLASS_NAME, "changeNotify");
	};

	private NotificationListener removeAccountListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "removeNotify", notification);
		Account account = (Account) notification.subject().get();
		if (bank.equals(account.owner().owner())) {
			removeAccountNotification(account);
			updateMenuItems();
		}
		LOGGER.exiting(CLASS_NAME, "removeNotify");
	};

	private NotificationListener transactionListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "transactionNotify");
		updateMenuItems();
		LOGGER.exiting(CLASS_NAME, "transactionNotify");
	};

	public BankPanel(Bank bank, IBankApplication application) {
		LOGGER.entering(CLASS_NAME, "init", bank);
		setLayout(new BorderLayout());
		this.bank = bank;
		JPanel buttonPanel = new BottomColoredPanel();
		List<Account> accounts = bank
				.branches()
				.flatMap(branch -> branch.accounts())
				.sorted()
				.collect(Collectors.toList());
		accountsPanel = new AccountsPanel(accounts, application, bank);
		add(accountsPanel, BorderLayout.CENTER);
		exit = new JButton(BankActionFactory.instance().exitApplicationAction());
		buttonPanel.add(exit);
		addListeners();
	}

	public Bank selectedBank() {
		return bank;
	}

	public void tabSelectionChanged() {
		LOGGER.entering(CLASS_NAME, "tabSelectionChanged");
		updateMenuItems();
		LOGGER.exiting(CLASS_NAME, "tabSelectionChanged");
	}

	public boolean owns(Bank bank) {
		return this.bank.equals(bank);
	}

	public Account selectedAccount() {
		LOGGER.entering(CLASS_NAME, "selectedAccount");
		Account account = accountsPanel.selectedAccount();
		LOGGER.exiting(CLASS_NAME, "selectedAccount", account);
		return account;
	}

	private void updateMenuItems() {
	}

	private void addAccountNotification(Account account) {
		LOGGER.entering(CLASS_NAME, "addAccountNotification", account);
		accountsPanel.addAccountNotification(account);
		LOGGER.exiting(CLASS_NAME, "addAccountNotification");
	}

	private void changeAccountNotification() {
		LOGGER.entering(CLASS_NAME, "changeAccountNotification");
		accountsPanel.changeAccountNotification();
		LOGGER.exiting(CLASS_NAME, "changeAccountNotification");
	}

	private void removeAccountNotification(Account account) {
		LOGGER.entering(CLASS_NAME, "removeAccountNotification", account);
		accountsPanel.removeAccountNotification(account);
		LOGGER.exiting(CLASS_NAME, "removeAccountNotification");
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "addListeners");
		NotificationCentre.addListener(addAccountListener, AccountNotificationType.Add);
		NotificationCentre.addListener(changeAccountListener, AccountNotificationType.Changed);
		NotificationCentre.addListener(removeAccountListener, AccountNotificationType.Removed);
		NotificationCentre.addListener(transactionListener, TransactionNotificationType.Add);
		NotificationCentre.addListener(transactionListener, TransactionNotificationType.Removed);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		accountsPanel.removeListeners();
		NotificationCentre.removeListener(addAccountListener);
		NotificationCentre.removeListener(changeAccountListener);
		NotificationCentre.removeListener(removeAccountListener);
		NotificationCentre.removeListener(transactionListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

}
