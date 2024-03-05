package applications.bank.gui.actions;

import applications.bank.gui.IApplication;

public class BankActionFactory {
	private IApplication application;
	private static BankActionFactory factory = null;

	private PreferencesAction preferencesAction = null;
	private PrintAction printAction = null;
	private ExitApplicationAction exitAction = null;
	private UndoAction undoAction = null;
	private RedoAction redoAction = null;
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
	private HelpAboutAction helpAboutAction = null;

	public static BankActionFactory instance(IApplication application) {
		if (factory == null) {
			factory = new BankActionFactory(application);
		}
		return factory;
	}

	private BankActionFactory(IApplication application) {
		this.application = application;
	}

	public PreferencesAction preferencesAction() {
		if (preferencesAction == null) {
			preferencesAction = new PreferencesAction(application);
		}
		return preferencesAction;
	}

	public PrintAction printAction() {
		if (printAction == null) {
			printAction = new PrintAction(application);
		}
		return printAction;
	}

	public ExitApplicationAction exitAction() {
		if (exitAction == null) {
			exitAction = new ExitApplicationAction(application);
		}
		return exitAction;
	}

	public UndoAction undoAction() {
		if (undoAction == null) {
			undoAction = new UndoAction(application);
		}
		return undoAction;
	}

	public RedoAction redoAction() {
		if (redoAction == null) {
			redoAction = new RedoAction(application);
		}
		return redoAction;
	}

	public AddBankAction addBankAction() {
		if (addBankAction == null) {
			addBankAction = new AddBankAction(application);
		}
		return addBankAction;
	}

	public RemoveBankAction removeBankAction() {
		if (removeBankAction == null) {
			removeBankAction = new RemoveBankAction(application);
		}
		return removeBankAction;
	}

	public AddAccountAction addAccountAction() {
		if (addAccountAction == null) {
			addAccountAction = new AddAccountAction(application);
		}
		return addAccountAction;
	}

	public RemoveAccountAction removeAccountAction() {
		if (removeAccountAction == null) {
			removeAccountAction = new RemoveAccountAction(application);
		}
		return removeAccountAction;
	}

	public AddInvestmentAction addInvestmentAction() {
		if (addInvestmentAction == null) {
			addInvestmentAction = new AddInvestmentAction(application);
		}
		return addInvestmentAction;
	}

	public ChangeInvestmentAction changeInvestmentAction() {
		if (changeInvestmentAction == null) {
			changeInvestmentAction = new ChangeInvestmentAction(application);
		}
		return changeInvestmentAction;
	}

	public RemoveInvestmentAction removeInvestmentAction() {
		if (removeInvestmentAction == null) {
			removeInvestmentAction = new RemoveInvestmentAction(application);
		}
		return removeInvestmentAction;
	}

	public AddStandingOrderAction addStandingOrderAction() {
		if (addStandingOrderAction == null) {
			addStandingOrderAction = new AddStandingOrderAction(application);
		}
		return addStandingOrderAction;
	}

	public ChangeStandingOrderAction changeStandingOrderAction() {
		if (changeStandingOrderAction == null) {
			changeStandingOrderAction = new ChangeStandingOrderAction(application);
		}
		return changeStandingOrderAction;
	}

	public RemoveStandingOrderAction removeStandingOrderAction() {
		if (removeStandingOrderAction == null) {
			removeStandingOrderAction = new RemoveStandingOrderAction(application);
		}
		return removeStandingOrderAction;
	}

	public PayMoneyInAction payMoneyInAction() {
		if (payMoneyInAction == null) {
			payMoneyInAction = new PayMoneyInAction(application);
		}
		return payMoneyInAction;
	}

	public PaySomeoneAction paySomeoneAction() {
		if (paySomeoneAction == null) {
			paySomeoneAction = new PaySomeoneAction(application);
		}
		return paySomeoneAction;
	}

	public TransferAction transferAction() {
		if (transferAction == null) {
			transferAction = new TransferAction(application);
		}
		return transferAction;
	}

	public ViewTransactionsAction viewTransactionsAction() {
		if (viewTransactionsAction == null) {
			viewTransactionsAction = new ViewTransactionsAction(application);
		}
		return viewTransactionsAction;
	}

	public ViewStandingOrdersAction viewStandingOrdersAction() {
		if (viewStandingOrdersAction == null) {
			viewStandingOrdersAction = new ViewStandingOrdersAction(application);
		}
		return viewStandingOrdersAction;
	}

	public ViewBankPercentagesAction viewBankPercentagesAction() {
		if (viewBankPercentagesAction == null) {
			viewBankPercentagesAction = new ViewBankPercentagesAction(application);
		}
		return viewBankPercentagesAction;
	}

	public ViewAccountBalanceHistoryAction viewAccountBalanceHistoryAction() {
		if (viewAccountBalanceHistoryAction == null) {
			viewAccountBalanceHistoryAction = new ViewAccountBalanceHistoryAction(application);
		}
		return viewAccountBalanceHistoryAction;
	}

	public ViewBankBalanceHistoryAction viewBankBalanceHistoryAction() {
		if (viewBankBalanceHistoryAction == null) {
			viewBankBalanceHistoryAction = new ViewBankBalanceHistoryAction(application);
		}
		return viewBankBalanceHistoryAction;
	}

	public ViewBanksBalanceHistoryAction viewBanksBalanceHistoryAction() {
		if (viewBanksBalanceHistoryAction == null) {
			viewBanksBalanceHistoryAction = new ViewBanksBalanceHistoryAction(application);
		}
		return viewBanksBalanceHistoryAction;
	}

	public ViewInvestmentPercentagesAction viewInvestmentPercentagesAction() {
		if (viewInvestmentPercentagesAction == null) {
			viewInvestmentPercentagesAction = new ViewInvestmentPercentagesAction(application);
		}
		return viewInvestmentPercentagesAction;
	}

	public ViewInvestmentHistoryAction viewInvestmentHistroyAction() {
		if (viewInvestmentHistoryAction == null) {
			viewInvestmentHistoryAction = new ViewInvestmentHistoryAction(application);
		}
		return viewInvestmentHistoryAction;
	}

	public ViewTotalInvestmentHistoryAction viewTotalInvestmentHistoryAction() {
		if (viewTotalInvestmentHistoryAction == null) {
			viewTotalInvestmentHistoryAction = new ViewTotalInvestmentHistoryAction(application);
		}
		return viewTotalInvestmentHistoryAction;
	}

	public HelpAboutAction helpAboutAction() {
		if (helpAboutAction == null) {
			helpAboutAction = new HelpAboutAction(application);
		}
		return helpAboutAction;
	}
}
