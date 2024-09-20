package applications.bank.gui.modified;

import java.awt.Component;
import java.util.logging.Logger;

import application.base.app.gui.ColoredTabbedPane;
import application.definition.ApplicationConfiguration;
import applications.bank.application.IBankApplication;
import applications.bank.gui.actions.BankActionFactory;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.model.Investment;

public class MainBankTabbedPane extends ColoredTabbedPane {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = MainBankTabbedPane.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();
	private SummaryPanel summaryPanel = null;
	private BankTabbedPane bankPane = null;
	private InvestmentTabbedPane investmentPane = null;

	public MainBankTabbedPane(IBankApplication application) {
		LOGGER.entering(CLASS_NAME, "init");
		setTabPlacement(LEFT);
		this.addChangeListener((e) -> {
			tabSelectionChanged();
		});
		summaryPanel = new SummaryPanel();
		bankPane = new BankTabbedPane(application);
		investmentPane = new InvestmentTabbedPane(application);
		this.addTab("Summary", summaryPanel);
		this.addTab("banks", bankPane);
		this.addTab("Investments", investmentPane);
		if (ApplicationConfiguration.applicationDefinition().bottomColor().isPresent()) {
			this.setBackgroundAt(0, ApplicationConfiguration.applicationDefinition().bottomColor().get());
			this.setBackgroundAt(1, ApplicationConfiguration.applicationDefinition().bottomColor().get());
			this.setBackgroundAt(2, ApplicationConfiguration.applicationDefinition().bottomColor().get());
		}
		LOGGER.exiting(CLASS_NAME, "init");
	}

	private void tabSelectionChanged() {
		LOGGER.entering(CLASS_NAME, "tabSelectionChanged");
		Component component = getSelectedComponent();
		if (component instanceof SummaryPanel) {
			BankActionFactory.instance().summaryPanel();
			;
		} else if (component instanceof BankTabbedPane) {
			BankActionFactory.instance().bankTabbedPane();
			;
		} else {
			BankActionFactory.instance().investmentPane();
			;
		}
		LOGGER.exiting(CLASS_NAME, "tabSelectionChanged");
	}

	public Account selectedAccount() {
		LOGGER.entering(CLASS_NAME, "selectedAccount");
		Account account = bankTabbedPane().selectedAccount();
		LOGGER.exiting(CLASS_NAME, "selectedAccount", account);
		return account;
	}

	public Bank selectedBank() {
		LOGGER.entering(CLASS_NAME, "selectedBank");
		Bank bank = bankTabbedPane().selectedBank();
		LOGGER.exiting(CLASS_NAME, "selectedBank", bank);
		return bank;
	}

	public Investment selectedInvestment() {
		LOGGER.entering(CLASS_NAME, "selectedInvestment");
		Investment investment = investmentTabbedPane().selectedInvestment();
		LOGGER.exiting(CLASS_NAME, "selectedInvestment");
		return investment;
	}

	public BankTabbedPane bankTabbedPane() {
		LOGGER.entering(CLASS_NAME, "bankTabbedPane");
		LOGGER.exiting(CLASS_NAME, "bankTabbedPane");
		return bankPane;
	}

	public InvestmentTabbedPane investmentTabbedPane() {
		LOGGER.entering(CLASS_NAME, "investmentTabbedPane");
		LOGGER.exiting(CLASS_NAME, "investmentTabbedPane");
		return investmentPane;
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		bankPane.removeListeners();
		investmentPane.removeListeners();
		summaryPanel.removeListeners();
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}
}
