package applications.bank.gui.modified;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import application.base.app.gui.BottomColoredPanel;
import application.definition.ApplicationConfiguration;
import applications.bank.application.IBankApplication;
import applications.bank.gui.actions.BankActionFactory;
import applications.bank.gui.models.AccountsTableModel;
import applications.bank.model.Account;
import applications.bank.model.Bank;

public class AccountsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = AccountsPanel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	JTable accountsTable;
	private AccountsTableModel model;
	private JButton clearSelection = new JButton("Clear selection");
	private JPopupMenu popup;
	private BankActionFactory actionFactory;
	private Point popupLocation;
	private AccountsTableRenderer accountsTableRenderer;

	public AccountsPanel(List<Account> accounts, IBankApplication application, Bank bank) {
		LOGGER.entering(CLASS_NAME, "init", accounts);
		actionFactory = BankActionFactory.instance(application);
		accountsTableRenderer = new AccountsTableRenderer();
		setLayout(new BorderLayout());
		model = new AccountsTableModel(accounts, bank);
		accountsTable = new JTable(model) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void processMouseEvent(MouseEvent e) {
				if (e.isPopupTrigger() && actionFactory.viewTransactionsAction().isEnabled()) {
					popupLocation = e.getPoint();
					popup.show(this, e.getX(), e.getY());
				} else {
					super.processMouseEvent(e);
				}
			}
		};
		accountsTable.setDefaultRenderer(String.class, accountsTableRenderer);
		accountsTable.setDefaultRenderer(Number.class, accountsTableRenderer);
		configureAccountsTable();
		JScrollPane scrollPane = new JScrollPane(accountsTable);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);
		JPanel buttonPanel = new BottomColoredPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(clearSelection);
		add(buttonPanel, BorderLayout.PAGE_END);
		clearSelection.addActionListener((e) -> {
			accountsTable.getSelectionModel().clearSelection();
		});
		popup = new JPopupMenu();
		JMenu paymentMenu = new JMenu("Payment");
		paymentMenu.add(actionFactory.payMoneyInAction());
		paymentMenu.add(actionFactory.paySomeoneAction());
		paymentMenu.addSeparator();
		paymentMenu.add(actionFactory.transferAction());
		JMenu viewMenu = new JMenu("View");
		viewMenu.add(actionFactory.viewTransactionsAction());
		viewMenu.add(actionFactory.viewStandingOrdersAction());
		viewMenu.addSeparator();
		viewMenu.add(actionFactory.viewAccountBalanceHistoryAction());
		JMenu actionMenu = new JMenu("Actions");
		actionMenu.add(actionFactory.deactivateAccountAction());
		actionMenu.add(actionFactory.reactivateAccountAction());
		popup.add(paymentMenu);
		popup.add(viewMenu);
		popup.add(actionMenu);
		LOGGER.exiting(CLASS_NAME, "init");
	}

	public void addAccountNotification(Account account) {
		LOGGER.entering(CLASS_NAME, "addAccountNotification", account);
		model.addAccountNotification(account);
		LOGGER.exiting(CLASS_NAME, "addAccountNotification");
	}

	public void changeAccountNotification() {
		LOGGER.entering(CLASS_NAME, "changeAccountNotification");
		model.changeAccountNotification();
		LOGGER.exiting(CLASS_NAME, "changeAccountNotification");
	}

	public void removeAccountNotification(Account account) {
		LOGGER.entering(CLASS_NAME, "removeAccountNotification", account);
		model.removeAccountNotification(account);
		LOGGER.exiting(CLASS_NAME, "removeAccountNotification");
	}

	public Account selectedAccount() {
		LOGGER.entering(CLASS_NAME, "selectedAccount");
		Account account = null;
		if (popupLocation != null) {
			int row = accountsTable.rowAtPoint(popupLocation);
			account = model.account(row);
		}
		LOGGER.exiting(CLASS_NAME, "selectedAccount", account);
		return account;
	}

	private void configureAccountsTable() {
		LOGGER.entering(CLASS_NAME, "configureAccountsTable");
		accountsTable.setFillsViewportHeight(true);
		accountsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		accountsTable.setRowSelectionAllowed(true);
		accountsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		accountsTable.getColumnModel().getColumn(0).setPreferredWidth(170);
		accountsTable.getColumnModel().getColumn(0).setMaxWidth(170);
		accountsTable.getColumnModel().getColumn(1).setPreferredWidth(170);
		accountsTable.getColumnModel().getColumn(1).setMaxWidth(170);
		accountsTable.getColumnModel().getColumn(2).setPreferredWidth(170);
		accountsTable.getColumnModel().getColumn(2).setMaxWidth(800);
		accountsTable.getColumnModel().getColumn(3).setPreferredWidth(170);
		accountsTable.getColumnModel().getColumn(3).setMaxWidth(170);
		LOGGER.exiting(CLASS_NAME, "configureAccountsTable");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		model.removeListeners();
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

}
