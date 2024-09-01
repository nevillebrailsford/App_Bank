package applications.bank.gui;

import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import application.definition.ApplicationConfiguration;
import applications.bank.gui.actions.BankActionFactory;

public class BankApplicationMenu extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = BankApplicationMenu.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	@SuppressWarnings("unused")
	private IApplication application;
	private BankActionFactory actionFactory;

	private JMenu fileMenu = new JMenu("File");
	private JMenuItem preferences;
	private JMenuItem print;
	private JMenuItem exit;
	private JMenu editMenu = new JMenu("Edit");
	private JMenuItem undo;
	private JMenuItem redo;
	private JMenu addMenu = new JMenu("Add");
	private JMenuItem addBank;
	private JMenuItem addAccount;
	private JMenuItem addStandingOrder;
	private JMenuItem addInvestment;
	private JMenu updateMenu = new JMenu("Update");
	private JMenuItem changeInvestment;
	private JMenuItem changeStandingOrder;
	private JMenu removeMenu = new JMenu("Remove");
	private JMenuItem removeBank;
	private JMenuItem removeAccount;
	private JMenuItem removeInvestment;
	private JMenuItem removeStandingOrder;
	private JMenu paymentMenu = new JMenu("Payment");
	private JMenuItem payMoneyIn;
	private JMenuItem paySomeone;
	private JMenuItem transfer;
	private JMenu viewMenu = new JMenu("View");
	private JMenuItem viewBankPercentages;
	private JMenuItem viewInvestmentPercentages;
	private JMenuItem viewBanksBalanceHistory;
	private JMenuItem viewTotalInvestmentHistory;
	private JMenuItem viewTotalValueHistory;
	private JMenu searchMenu = new JMenu("Search");
	private JMenuItem searchTransactions;
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem helpAbout;

	public BankApplicationMenu(IApplication application) {
		LOGGER.entering(CLASS_NAME, "init");
		actionFactory = BankActionFactory.instance(application);
		this.application = application;
		add(fileMenu);
		preferences = new JMenuItem(actionFactory.preferencesAction());
		fileMenu.add(preferences);
		fileMenu.addSeparator();
		print = new JMenuItem(actionFactory.printAction());
		fileMenu.add(print);
		fileMenu.addSeparator();
		exit = new JMenuItem(actionFactory.exitAction());
		fileMenu.add(exit);
		add(editMenu);
		undo = new JMenuItem(actionFactory.undoAction());
		editMenu.add(undo);
		redo = new JMenuItem(actionFactory.redoAction());
		editMenu.add(redo);
		editMenu.addSeparator();
		editMenu.add(addMenu);
		addBank = new JMenuItem(actionFactory.addBankAction());
		addAccount = new JMenuItem(actionFactory.addAccountAction());
		addInvestment = new JMenuItem(actionFactory.addInvestmentAction());
		addStandingOrder = new JMenuItem(actionFactory.addStandingOrderAction());
		addMenu.add(addBank);
		addMenu.add(addAccount);
		addMenu.addSeparator();
		addMenu.add(addStandingOrder);
		addMenu.addSeparator();
		addMenu.add(addInvestment);
		editMenu.add(updateMenu);
		changeInvestment = new JMenuItem(actionFactory.changeInvestmentAction());
		changeStandingOrder = new JMenuItem(actionFactory.changeStandingOrderAction());
		updateMenu.add(changeStandingOrder);
		updateMenu.addSeparator();
		updateMenu.add(changeInvestment);
		removeBank = new JMenuItem(actionFactory.removeBankAction());
		removeAccount = new JMenuItem(actionFactory.removeAccountAction());
		removeInvestment = new JMenuItem(actionFactory.removeInvestmentAction());
		removeStandingOrder = new JMenuItem(actionFactory.removeStandingOrderAction());
		editMenu.add(removeMenu);
		removeMenu.add(removeBank);
		removeMenu.add(removeAccount);
		removeMenu.addSeparator();
		removeMenu.add(removeStandingOrder);
		removeMenu.addSeparator();
		removeMenu.add(removeInvestment);
		add(editMenu);
		viewBankPercentages = new JMenuItem(actionFactory.viewBankPercentagesAction());
		viewInvestmentPercentages = new JMenuItem(actionFactory.viewInvestmentPercentagesAction());
		viewTotalInvestmentHistory = new JMenuItem(actionFactory.viewTotalInvestmentHistoryAction());
		viewBanksBalanceHistory = new JMenuItem(actionFactory.viewBanksBalanceHistoryAction());
		viewTotalValueHistory = new JMenuItem(actionFactory.viewTotalValueHistoryAction());
		viewMenu.add(viewBankPercentages);
		viewMenu.add(viewInvestmentPercentages);
		viewMenu.addSeparator();
		viewMenu.add(viewBanksBalanceHistory);
		viewMenu.add(viewTotalInvestmentHistory);
		viewMenu.addSeparator();
		viewMenu.add(viewTotalValueHistory);
		add(viewMenu);
		payMoneyIn = new JMenuItem(actionFactory.payMoneyInAction());
		paySomeone = new JMenuItem(actionFactory.paySomeoneAction());
		transfer = new JMenuItem(actionFactory.transferAction());
		paymentMenu.add(payMoneyIn);
		paymentMenu.add(paySomeone);
		paymentMenu.addSeparator();
		paymentMenu.add(transfer);
		add(paymentMenu);
		searchTransactions = new JMenuItem(actionFactory.searchTransactionsAction());
		searchMenu.add(searchTransactions);
		add(searchMenu);
		helpAbout = new JMenuItem(actionFactory.helpAboutAction());
		helpMenu.add(helpAbout);
		add(helpMenu);
		LOGGER.exiting(CLASS_NAME, "init");
	}

	public void enableUndo(boolean enabled) {
		LOGGER.entering(CLASS_NAME, "enableUndo", enabled);
		undo.setEnabled(enabled);
		LOGGER.exiting(CLASS_NAME, "enableUndo");
	}

	public void enableRedo(boolean enabled) {
		LOGGER.entering(CLASS_NAME, "enableRedo", enabled);
		redo.setEnabled(enabled);
		LOGGER.exiting(CLASS_NAME, "enableRedo");
	}

	public void summaryPanel() {
		enableBankItems(false);
		enableInvestmentItems(false);
	}

	public void bankTabbedPane() {
		enableBankItems(true);
		enableInvestmentItems(false);
	}

	public void investmentPane() {
		enableBankItems(false);
		enableInvestmentItems(true);
	}

	public void accountSelected(boolean selected) {
		actionFactory.viewTransactionsAction().setEnabled(selected);
		actionFactory.viewStandingOrdersAction().setEnabled(selected);
	}

	private void enableBankItems(boolean enabled) {
		actionFactory.addBankAction().setEnabled(enabled);
		actionFactory.addAccountAction().setEnabled(enabled);
		actionFactory.removeBankAction().setEnabled(enabled);
		actionFactory.removeAccountAction().setEnabled(enabled);
		actionFactory.addStandingOrderAction().setEnabled(enabled);
		actionFactory.changeStandingOrderAction().setEnabled(enabled);
		actionFactory.removeStandingOrderAction().setEnabled(enabled);
		actionFactory.payMoneyInAction().setEnabled(enabled);
		actionFactory.paySomeoneAction().setEnabled(enabled);
		actionFactory.transferAction().setEnabled(enabled);
	}

	private void enableInvestmentItems(boolean enabled) {
		actionFactory.addInvestmentAction().setEnabled(enabled);
		actionFactory.changeInvestmentAction().setEnabled(enabled);
		actionFactory.removeInvestmentAction().setEnabled(enabled);
	}
}
