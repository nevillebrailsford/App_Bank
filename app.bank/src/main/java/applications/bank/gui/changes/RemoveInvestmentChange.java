package applications.bank.gui.changes;

import java.util.logging.Logger;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Investment;
import applications.bank.storage.BankMonitor;

public class RemoveInvestmentChange extends AbstractChange {
	private static final String CLASS_NAME = RemoveInvestmentChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Investment investment;

	public RemoveInvestmentChange(Investment investment) {
		this.investment = investment;
	}

	@Override
	protected void doHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "doHook");
		redoHook();
		LOGGER.exiting(CLASS_NAME, "doHook");
	}

	@Override
	protected void redoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "redoHook");
		BankMonitor.instance().removeInvestment(investment);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		BankMonitor.instance().addInvestment(investment);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

}
