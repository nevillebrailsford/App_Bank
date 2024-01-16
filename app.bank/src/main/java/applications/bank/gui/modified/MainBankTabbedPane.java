package applications.bank.gui.modified;

import java.awt.Component;
import java.util.logging.Logger;

import application.base.app.gui.ColoredTabbedPane;
import application.definition.ApplicationConfiguration;
import applications.bank.gui.BankApplicationMenu;
import applications.bank.gui.IApplication;
import applications.bank.model.Account;
import applications.bank.model.Investment;

public class MainBankTabbedPane extends ColoredTabbedPane {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = MainBankTabbedPane.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();
	private SummaryPanel summaryPanel = null;
	private BankTabbedPane bankPane = null;
	private InvestmentTabbedPane investmentPane = null;
	private BankApplicationMenu menuBar = null;

	public MainBankTabbedPane(BankApplicationMenu menuBar, IApplication application) {
		LOGGER.entering(CLASS_NAME, "init");
		this.menuBar = menuBar;
		setTabPlacement(LEFT);
		this.addChangeListener((e) -> {
			tabSelectionChanged();
		});
		summaryPanel = new SummaryPanel();
		bankPane = new BankTabbedPane(menuBar, application);
		investmentPane = new InvestmentTabbedPane(menuBar, application);
		this.addTab("Summary", summaryPanel);
		this.addTab("banks", bankPane);
		this.addTab("Investments", investmentPane);
		LOGGER.exiting(CLASS_NAME, "init");
	}

	private void tabSelectionChanged() {
		LOGGER.entering(CLASS_NAME, "tabSelectionChanged");
		Component component = getSelectedComponent();
		if (component instanceof SummaryPanel) {
			menuBar.summaryPanel();
			;
		} else if (component instanceof BankTabbedPane) {
			menuBar.bankTabbedPane();
			;
		} else {
			menuBar.investmentPane();
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
