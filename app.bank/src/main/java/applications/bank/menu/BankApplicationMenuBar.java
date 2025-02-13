package applications.bank.menu;

import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import application.definition.ApplicationConfiguration;
import application.menu.AbstractMenuBar;
import applications.bank.application.IBankApplication;
import applications.bank.gui.actions.BankActionFactory;

public class BankApplicationMenuBar extends AbstractMenuBar {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = BankApplicationMenuBar.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private JMenuItem print;
	private JMenuItem printSummary;
	private JMenuItem printTaxSummary;
	private JMenuItem addBank;
	private JMenuItem addAccount;
	private JMenuItem addStandingOrder;
	private JMenuItem addInvestment;
	private JMenuItem changeInvestment;
	private JMenuItem changeStandingOrder;
	private JMenuItem removeBank;
	private JMenuItem removeAccount;
	private JMenuItem removeInvestment;
	private JMenuItem removeStandingOrder;
	private JMenuItem payMoneyIn;
	private JMenuItem paySomeone;
	private JMenuItem transfer;
	private JMenuItem viewBankPercentages;
	private JMenuItem viewInvestmentPercentages;
	private JMenuItem viewBanksBalanceHistory;
	private JMenuItem viewTotalInvestmentHistory;
	private JMenuItem viewTotalValueHistory;
	private JMenuItem searchTransactions;

	public BankApplicationMenuBar(IBankApplication application) {
		super(BankActionFactory.instance(application));
		LOGGER.entering(CLASS_NAME, "init");
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	public void addBeforeExit(JMenu fileMenu) {
		LOGGER.entering(CLASS_NAME, "addBeforeExit");
		print = new JMenuItem(BankActionFactory.instance().printAction());
		printSummary = new JMenuItem(BankActionFactory.instance().printSummaryAction());
		printTaxSummary = new JMenuItem(BankActionFactory.instance().printTaxDetailsForYear());
		fileMenu.addSeparator();
		JMenu reportMenu = new JMenu("Reports");
		reportMenu.add(print);
		reportMenu.add(printSummary);
		reportMenu.add(printTaxSummary);
		fileMenu.add(reportMenu);
		LOGGER.exiting(CLASS_NAME, "addBeforeExit");
	}

	@Override
	public void addToEditMenu(JMenu editMenu) {
		LOGGER.entering(CLASS_NAME, "addToEditMenu");
		editMenu.addSeparator();
		addAddMenu(editMenu);
		addUpdateMenu(editMenu);
		addRemoveMenu(editMenu);
		LOGGER.exiting(CLASS_NAME, "addToEditMenu");
	}

	@Override
	public void addAdditionalMenus(JMenuBar menuBar) {
		LOGGER.entering(CLASS_NAME, "addAdditionalMenus");
		addViewMenu(menuBar);
		addPaymentMenu(menuBar);
		addSearchMenu(menuBar);
		LOGGER.exiting(CLASS_NAME, "addAdditionalMenus");
	}

	private void addAddMenu(JMenu editMenu) {
		LOGGER.entering(CLASS_NAME, "addAddMenu");
		JMenu addMenu = new JMenu("Add");
		editMenu.add(addMenu);
		addBank = new JMenuItem(BankActionFactory.instance().addBankAction());
		addAccount = new JMenuItem(BankActionFactory.instance().addAccountAction());
		addInvestment = new JMenuItem(BankActionFactory.instance().addInvestmentAction());
		addStandingOrder = new JMenuItem(BankActionFactory.instance().addStandingOrderAction());
		addMenu.add(addBank);
		addMenu.add(addAccount);
		addMenu.addSeparator();
		addMenu.add(addStandingOrder);
		addMenu.addSeparator();
		addMenu.add(addInvestment);
		LOGGER.exiting(CLASS_NAME, "addAddMenu");
	}

	private void addUpdateMenu(JMenu editMenu) {
		LOGGER.entering(CLASS_NAME, "addUpdateMenu");
		JMenu updateMenu = new JMenu("Update");
		editMenu.add(updateMenu);
		changeInvestment = new JMenuItem(BankActionFactory.instance().changeInvestmentAction());
		changeStandingOrder = new JMenuItem(BankActionFactory.instance().changeStandingOrderAction());
		updateMenu.add(changeStandingOrder);
		updateMenu.addSeparator();
		updateMenu.add(changeInvestment);
		LOGGER.exiting(CLASS_NAME, "addUpdateMenu");
	}

	private void addRemoveMenu(JMenu editMenu) {
		LOGGER.entering(CLASS_NAME, "addRemoveMenu");
		JMenu removeMenu = new JMenu("Remove");
		editMenu.add(removeMenu);
		removeBank = new JMenuItem(BankActionFactory.instance().removeBankAction());
		removeAccount = new JMenuItem(BankActionFactory.instance().removeAccountAction());
		removeInvestment = new JMenuItem(BankActionFactory.instance().removeInvestmentAction());
		removeStandingOrder = new JMenuItem(BankActionFactory.instance().removeStandingOrderAction());
		removeMenu.add(removeBank);
		removeMenu.add(removeAccount);
		removeMenu.addSeparator();
		removeMenu.add(removeStandingOrder);
		removeMenu.addSeparator();
		removeMenu.add(removeInvestment);
		LOGGER.exiting(CLASS_NAME, "addRemoveMenu");
	}

	private void addViewMenu(JMenuBar menuBar) {
		LOGGER.entering(CLASS_NAME, "addViewMenu");
		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);
		viewBankPercentages = new JMenuItem(BankActionFactory.instance().viewBankPercentagesAction());
		viewInvestmentPercentages = new JMenuItem(BankActionFactory.instance().viewInvestmentPercentagesAction());
		viewTotalInvestmentHistory = new JMenuItem(BankActionFactory.instance().viewTotalInvestmentHistoryAction());
		viewBanksBalanceHistory = new JMenuItem(BankActionFactory.instance().viewBanksBalanceHistoryAction());
		viewTotalValueHistory = new JMenuItem(BankActionFactory.instance().viewTotalValueHistoryAction());
		viewMenu.add(viewBankPercentages);
		viewMenu.add(viewInvestmentPercentages);
		viewMenu.addSeparator();
		viewMenu.add(viewBanksBalanceHistory);
		viewMenu.add(viewTotalInvestmentHistory);
		viewMenu.addSeparator();
		viewMenu.add(viewTotalValueHistory);
		LOGGER.exiting(CLASS_NAME, "addViewMenu");
	}

	private void addPaymentMenu(JMenuBar menuBar) {
		LOGGER.entering(CLASS_NAME, "addPaymentMenu");
		JMenu paymentMenu = new JMenu("Payment");
		menuBar.add(paymentMenu);
		payMoneyIn = new JMenuItem(BankActionFactory.instance().payMoneyInAction());
		paySomeone = new JMenuItem(BankActionFactory.instance().paySomeoneAction());
		transfer = new JMenuItem(BankActionFactory.instance().transferAction());
		paymentMenu.add(payMoneyIn);
		paymentMenu.add(paySomeone);
		paymentMenu.addSeparator();
		paymentMenu.add(transfer);
		LOGGER.exiting(CLASS_NAME, "addPaymentMenu");
	}

	private void addSearchMenu(JMenuBar menuBar) {
		LOGGER.entering(CLASS_NAME, "addSearchMenu");
		JMenu searchMenu = new JMenu("Search");
		menuBar.add(searchMenu);
		searchTransactions = new JMenuItem(BankActionFactory.instance().searchTransactionsAction());
		searchMenu.add(searchTransactions);
		LOGGER.exiting(CLASS_NAME, "addSearchMenu");
	}

}
