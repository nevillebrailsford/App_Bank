package applications.bank.gui;

public interface IApplication {
	public void preferencesAction();

	public void undoAction();

	public void redoAction();

	public void exitApplicationAction();

	public void printAction();

	public void addBankAction();

	public void removeBankAction();

	public void addAccountAction();

	public void removeAccountAction();

	public void addInvestmentAction();

	public void changeInvestmentAction();

	public void removeInvestmentAction();

	public void addStandingOrderAction();

	public void changeStandingOrderAction();

	public void removeStandingOrderAction();

	public void payMoneyInAction();

	public void paySomeoneAction();

	public void transferAction();

	public void viewTransactionsAction();

	public void viewAccountBalanceHistoryAction();

	public void viewBankBalanceHistoryAction();

	public void viewBanksBalanceHistoryAction();

	public void viewStandingOrdersAction();

	public void viewBankPercentagesAction();

	public void viewInvestmentPercentagesAction();

	public void viewInvestmentHistoryAction();

	public void viewTotalInvestmentHistoryAction();

	public void helpAboutAction();
}
