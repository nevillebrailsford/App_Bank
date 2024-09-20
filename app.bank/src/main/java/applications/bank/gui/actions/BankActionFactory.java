package applications.bank.gui.actions;

import java.util.logging.Logger;

import javax.swing.JOptionPane;

import application.action.BaseActionFactory;
import application.definition.ApplicationConfiguration;
import applications.bank.application.IBankApplication;

public class BankActionFactory extends BaseActionFactory {
	private static final String CLASS_NAME = BankActionFactory.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private static BankActionFactory instance = null;

	private PrintAction printAction = null;
	private AddBankAction addBankAction = null;
	private RemoveBankAction removeBankAction = null;
	private AddAccountAction addAccountAction;
	private RemoveAccountAction removeAccountAction;
	private AddInvestmentAction addInvestmentAction;
	private ChangeInvestmentAction changeInvestmentAction;
	private ChangeStandingOrderAction changeStandingOrderAction;
	private RemoveInvestmentAction removeInvestmentAction;
	private AddStandingOrderAction addStandingOrderAction;
	private RemoveStandingOrderAction removeStandingOrderAction;
	private PayMoneyInAction payMoneyInAction;
	private PaySomeoneAction paySomeoneAction;
	private TransferAction transferAction;
	private ViewTransactionsAction viewTransactionsAction;
	private ViewStandingOrdersAction viewStandingOrdersAction;
	private ViewBankPercentagesAction viewBankPercentagesAction;
	private ViewAccountBalanceHistoryAction viewAccountBalanceHistoryAction;
	private ViewBankBalanceHistoryAction viewBankBalanceHistoryAction;
	private ViewBanksBalanceHistoryAction viewBanksBalanceHistoryAction;
	private ViewTotalInvestmentHistoryAction viewTotalInvestmentHistoryAction;
	private ViewInvestmentPercentagesAction viewInvestmentPercentagesAction;
	private ViewInvestmentHistoryAction viewInvestmentHistoryAction;
	private ViewTotalValueHistoryAction viewTotalValueHistoryAction;
	private SearchTransactionsAction searchTransactionsAction;

	public static BankActionFactory instance(IBankApplication... application) {
		LOGGER.entering(CLASS_NAME, "instance", application);
		if (instance == null) {
			if (application.length == 0) {
				JOptionPane.showMessageDialog(null, "Application was not specified on first call to instance.",
						"ActionFactory error.", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
			instance = new BankActionFactory();
			instance.application = application[0];

		}
		LOGGER.exiting(CLASS_NAME, "");
		return instance;
	}

	public BankActionFactory() {
		super();
	}

	public PrintAction printAction() {
		if (printAction == null) {
			printAction = new PrintAction((IBankApplication) application);
		}
		return printAction;
	}

	public AddBankAction addBankAction() {
		if (addBankAction == null) {
			addBankAction = new AddBankAction((IBankApplication) application);
		}
		return addBankAction;
	}

	public RemoveBankAction removeBankAction() {
		if (removeBankAction == null) {
			removeBankAction = new RemoveBankAction((IBankApplication) application);
		}
		return removeBankAction;
	}

	public AddAccountAction addAccountAction() {
		if (addAccountAction == null) {
			addAccountAction = new AddAccountAction((IBankApplication) application);
		}
		return addAccountAction;
	}

	public RemoveAccountAction removeAccountAction() {
		if (removeAccountAction == null) {
			removeAccountAction = new RemoveAccountAction((IBankApplication) application);
		}
		return removeAccountAction;
	}

	public AddInvestmentAction addInvestmentAction() {
		if (addInvestmentAction == null) {
			addInvestmentAction = new AddInvestmentAction((IBankApplication) application);
		}
		return addInvestmentAction;
	}

	public ChangeInvestmentAction changeInvestmentAction() {
		if (changeInvestmentAction == null) {
			changeInvestmentAction = new ChangeInvestmentAction((IBankApplication) application);
		}
		return changeInvestmentAction;
	}

	public RemoveInvestmentAction removeInvestmentAction() {
		if (removeInvestmentAction == null) {
			removeInvestmentAction = new RemoveInvestmentAction((IBankApplication) application);
		}
		return removeInvestmentAction;
	}

	public AddStandingOrderAction addStandingOrderAction() {
		if (addStandingOrderAction == null) {
			addStandingOrderAction = new AddStandingOrderAction((IBankApplication) application);
		}
		return addStandingOrderAction;
	}

	public ChangeStandingOrderAction changeStandingOrderAction() {
		if (changeStandingOrderAction == null) {
			changeStandingOrderAction = new ChangeStandingOrderAction((IBankApplication) application);
		}
		return changeStandingOrderAction;
	}

	public RemoveStandingOrderAction removeStandingOrderAction() {
		if (removeStandingOrderAction == null) {
			removeStandingOrderAction = new RemoveStandingOrderAction((IBankApplication) application);
		}
		return removeStandingOrderAction;
	}

	public PayMoneyInAction payMoneyInAction() {
		if (payMoneyInAction == null) {
			payMoneyInAction = new PayMoneyInAction((IBankApplication) application);
		}
		return payMoneyInAction;
	}

	public PaySomeoneAction paySomeoneAction() {
		if (paySomeoneAction == null) {
			paySomeoneAction = new PaySomeoneAction((IBankApplication) application);
		}
		return paySomeoneAction;
	}

	public TransferAction transferAction() {
		if (transferAction == null) {
			transferAction = new TransferAction((IBankApplication) application);
		}
		return transferAction;
	}

	public ViewTransactionsAction viewTransactionsAction() {
		if (viewTransactionsAction == null) {
			viewTransactionsAction = new ViewTransactionsAction((IBankApplication) application);
		}
		return viewTransactionsAction;
	}

	public ViewStandingOrdersAction viewStandingOrdersAction() {
		if (viewStandingOrdersAction == null) {
			viewStandingOrdersAction = new ViewStandingOrdersAction((IBankApplication) application);
		}
		return viewStandingOrdersAction;
	}

	public ViewBankPercentagesAction viewBankPercentagesAction() {
		if (viewBankPercentagesAction == null) {
			viewBankPercentagesAction = new ViewBankPercentagesAction((IBankApplication) application);
		}
		return viewBankPercentagesAction;
	}

	public ViewAccountBalanceHistoryAction viewAccountBalanceHistoryAction() {
		if (viewAccountBalanceHistoryAction == null) {
			viewAccountBalanceHistoryAction = new ViewAccountBalanceHistoryAction((IBankApplication) application);
		}
		return viewAccountBalanceHistoryAction;
	}

	public ViewBankBalanceHistoryAction viewBankBalanceHistoryAction() {
		if (viewBankBalanceHistoryAction == null) {
			viewBankBalanceHistoryAction = new ViewBankBalanceHistoryAction((IBankApplication) application);
		}
		return viewBankBalanceHistoryAction;
	}

	public ViewBanksBalanceHistoryAction viewBanksBalanceHistoryAction() {
		if (viewBanksBalanceHistoryAction == null) {
			viewBanksBalanceHistoryAction = new ViewBanksBalanceHistoryAction((IBankApplication) application);
		}
		return viewBanksBalanceHistoryAction;
	}

	public ViewInvestmentPercentagesAction viewInvestmentPercentagesAction() {
		if (viewInvestmentPercentagesAction == null) {
			viewInvestmentPercentagesAction = new ViewInvestmentPercentagesAction((IBankApplication) application);
		}
		return viewInvestmentPercentagesAction;
	}

	public ViewInvestmentHistoryAction viewInvestmentHistroyAction() {
		if (viewInvestmentHistoryAction == null) {
			viewInvestmentHistoryAction = new ViewInvestmentHistoryAction((IBankApplication) application);
		}
		return viewInvestmentHistoryAction;
	}

	public ViewTotalInvestmentHistoryAction viewTotalInvestmentHistoryAction() {
		if (viewTotalInvestmentHistoryAction == null) {
			viewTotalInvestmentHistoryAction = new ViewTotalInvestmentHistoryAction((IBankApplication) application);
		}
		return viewTotalInvestmentHistoryAction;
	}

	public ViewTotalValueHistoryAction viewTotalValueHistoryAction() {
		if (viewTotalValueHistoryAction == null) {
			viewTotalValueHistoryAction = new ViewTotalValueHistoryAction((IBankApplication) application);
		}
		return viewTotalValueHistoryAction;
	}

	public SearchTransactionsAction searchTransactionsAction() {
		if (searchTransactionsAction == null) {
			searchTransactionsAction = new SearchTransactionsAction((IBankApplication) application);
		}
		return searchTransactionsAction;
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
		viewTransactionsAction().setEnabled(selected);
		viewStandingOrdersAction().setEnabled(selected);
	}

	private void enableBankItems(boolean enabled) {
		addBankAction().setEnabled(enabled);
		addAccountAction().setEnabled(enabled);
		removeBankAction().setEnabled(enabled);
		removeAccountAction().setEnabled(enabled);
		addStandingOrderAction().setEnabled(enabled);
		changeStandingOrderAction().setEnabled(enabled);
		removeStandingOrderAction().setEnabled(enabled);
		payMoneyInAction().setEnabled(enabled);
		paySomeoneAction().setEnabled(enabled);
		transferAction().setEnabled(enabled);
	}

	private void enableInvestmentItems(boolean enabled) {
		addInvestmentAction().setEnabled(enabled);
		changeInvestmentAction().setEnabled(enabled);
		removeInvestmentAction().setEnabled(enabled);
	}

}
