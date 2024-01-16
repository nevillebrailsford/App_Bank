package applications.bank.gui.modified;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import applications.bank.gui.BankApplicationMenu;
import applications.bank.gui.IApplication;
import applications.bank.gui.actions.BankActionFactory;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.model.Branch;
import applications.bank.storage.AccountNotificationType;
import applications.bank.storage.TransactionNotificationType;

public class BankPanel extends ColoredPanel {
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = BankPanel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	private Bank bank;
	private BankApplicationMenu menuBar;
	private BankActionFactory actionFactory;
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

	public BankPanel(Bank bank, BankApplicationMenu menuBar, IApplication application) {
		LOGGER.entering(CLASS_NAME, "init", bank);
		actionFactory = BankActionFactory.instance(application);
		setLayout(new BorderLayout());
		this.bank = bank;
		this.menuBar = menuBar;
		JPanel buttonPanel = new BottomColoredPanel();
		List<Account> accounts = new ArrayList<>();
		for (Branch branch : bank.branches()) {
			accounts.addAll(branch.accounts());
		}
		Collections.sort(accounts);
		accountsPanel = new AccountsPanel(accounts, menuBar, application, bank);
		add(accountsPanel, BorderLayout.CENTER);
		exit = new JButton(actionFactory.exitAction());
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
		menuBar.enableRedo(ChangeManager.instance().redoable());
		menuBar.enableUndo(ChangeManager.instance().undoable());
	}

	private void addAccountNotification(Account account) {
		LOGGER.entering(CLASS_NAME, "addAccountNotification", account);
		accountsPanel.addAccountNotification(account);
		LOGGER.exiting(CLASS_NAME, "addAccountNotification");
	}

	private void removeAccountNotification(Account account) {
		LOGGER.entering(CLASS_NAME, "removeAccountNotification", account);
		accountsPanel.removeAccountNotification(account);
		LOGGER.exiting(CLASS_NAME, "removeAccountNotification");
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "addListeners");
		NotificationCentre.addListener(addAccountListener, AccountNotificationType.Add);
		NotificationCentre.addListener(removeAccountListener, AccountNotificationType.Removed);
		NotificationCentre.addListener(transactionListener, TransactionNotificationType.Add);
		NotificationCentre.addListener(transactionListener, TransactionNotificationType.Removed);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		accountsPanel.removeListeners();
		NotificationCentre.removeListener(addAccountListener);
		NotificationCentre.removeListener(removeAccountListener);
		NotificationCentre.removeListener(transactionListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

}
