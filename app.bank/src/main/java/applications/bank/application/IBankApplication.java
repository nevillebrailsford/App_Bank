package applications.bank.application;

import application.base.app.IApplication;

public interface IBankApplication extends IApplication {
	public void printAction();

	public void printSummaryAction();

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

	public void searchTransactionsAction();

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

	public void viewTotalValueHistoryAction();

	public void deactivateAccount();

	public void reactivateAccount();

}
