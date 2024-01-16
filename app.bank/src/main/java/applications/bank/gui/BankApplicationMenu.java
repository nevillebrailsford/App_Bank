package applications.bank.gui;

import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import application.base.app.gui.ColoredMenu;
import application.base.app.gui.ColoredMenuItem;
import application.definition.ApplicationConfiguration;
import applications.bank.gui.actions.BankActionFactory;

public class BankApplicationMenu extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = BankApplicationMenu.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	@SuppressWarnings("unused")
	private IApplication application;
	private BankActionFactory actionFactory;

	private JMenu fileMenu = new ColoredMenu("File");
	private JMenuItem preferences;
	private JMenuItem print;
	private JMenuItem exit;
	private JMenu editMenu = new ColoredMenu("Edit");
	private JMenuItem undo;
	private JMenuItem redo;
	private JMenu addMenu = new ColoredMenu("Add");
	private JMenuItem addBank;
	private JMenuItem addAccount;
	private JMenuItem addStandingOrder;
	private JMenuItem addInvestment;
	private JMenu updateMenu = new ColoredMenu("Update");
	private JMenuItem changeInvestment;
	private JMenuItem changeStandingOrder;
	private JMenu removeMenu = new ColoredMenu("Remove");
	private JMenuItem removeBank;
	private JMenuItem removeAccount;
	private JMenuItem removeInvestment;
	private JMenuItem removeStandingOrder;
	private JMenu paymentMenu = new ColoredMenu("Payment");
	private JMenuItem payMoneyIn;
	private JMenuItem paySomeone;
	private JMenuItem transfer;
	private JMenu viewMenu = new ColoredMenu("View");
	private JMenuItem viewBankPercentages;
	private JMenuItem viewInvestmentPercentages;
	private JMenuItem viewTotalInvestmentHistory;
	private JMenu helpMenu = new ColoredMenu("Help");
	private JMenuItem helpAbout;

	public BankApplicationMenu(IApplication application) {
		setOpaque(true);
		setBackground(ApplicationConfiguration.applicationDefinition().bottomColor().get());
		LOGGER.entering(CLASS_NAME, "init");
		actionFactory = BankActionFactory.instance(application);
		this.application = application;
		add(fileMenu);
		preferences = new ColoredMenuItem(actionFactory.preferencesAction());
		fileMenu.add(preferences);
		fileMenu.addSeparator();
		print = new ColoredMenuItem(actionFactory.printAction());
		fileMenu.add(print);
		fileMenu.addSeparator();
		exit = new ColoredMenuItem(actionFactory.exitAction());
		fileMenu.add(exit);
		add(editMenu);
		undo = new ColoredMenuItem(actionFactory.undoAction());
		editMenu.add(undo);
		redo = new ColoredMenuItem(actionFactory.redoAction());
		editMenu.add(redo);
		editMenu.addSeparator();
		editMenu.add(addMenu);
		addBank = new ColoredMenuItem(actionFactory.addBankAction());
		addAccount = new ColoredMenuItem(actionFactory.addAccountAction());
		addInvestment = new ColoredMenuItem(actionFactory.addInvestmentAction());
		addStandingOrder = new ColoredMenuItem(actionFactory.addStandingOrderAction());
		addMenu.add(addBank);
		addMenu.add(addAccount);
		addMenu.addSeparator();
		addMenu.add(addStandingOrder);
		addMenu.addSeparator();
		addMenu.add(addInvestment);
		editMenu.add(updateMenu);
		changeInvestment = new ColoredMenuItem(actionFactory.changeInvestmentAction());
		changeStandingOrder = new ColoredMenuItem(actionFactory.changeStandingOrderAction());
		updateMenu.add(changeStandingOrder);
		updateMenu.addSeparator();
		updateMenu.add(changeInvestment);
		removeBank = new ColoredMenuItem(actionFactory.removeBankAction());
		removeAccount = new ColoredMenuItem(actionFactory.removeAccountAction());
		removeInvestment = new ColoredMenuItem(actionFactory.removeInvestmentAction());
		removeStandingOrder = new ColoredMenuItem(actionFactory.removeStandingOrderAction());
		editMenu.add(removeMenu);
		removeMenu.add(removeBank);
		removeMenu.add(removeAccount);
		removeMenu.addSeparator();
		removeMenu.add(removeStandingOrder);
		removeMenu.addSeparator();
		removeMenu.add(removeInvestment);
		add(editMenu);
		viewBankPercentages = new ColoredMenuItem(actionFactory.viewBankPercentagesAction());
		viewInvestmentPercentages = new ColoredMenuItem(actionFactory.viewInvestmentPercentagesAction());
		viewTotalInvestmentHistory = new ColoredMenuItem(actionFactory.viewTotalInvestmentHistoryAction());
		viewMenu.add(viewBankPercentages);
		viewMenu.add(viewInvestmentPercentages);
		viewMenu.addSeparator();
		viewMenu.add(viewTotalInvestmentHistory);
		add(viewMenu);
		payMoneyIn = new ColoredMenuItem(actionFactory.payMoneyInAction());
		paySomeone = new ColoredMenuItem(actionFactory.paySomeoneAction());
		transfer = new ColoredMenuItem(actionFactory.transferAction());
		paymentMenu.add(payMoneyIn);
		paymentMenu.add(paySomeone);
		paymentMenu.addSeparator();
		paymentMenu.add(transfer);
		add(paymentMenu);
		helpAbout = new ColoredMenuItem(actionFactory.helpAboutAction());
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
