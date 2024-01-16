package applications.bank.gui.changes;

import java.util.logging.Logger;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Investment;
import applications.bank.storage.BankMonitor;

public class ChangeInvestmentChange extends AbstractChange {
	private static final String CLASS_NAME = ChangeInvestmentChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Investment oldInvestment;
	private Investment changedInvestment;

	public ChangeInvestmentChange(Investment oldInvestment, Investment changedInvestment) {
		this.oldInvestment = oldInvestment;
		this.changedInvestment = changedInvestment;
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
		BankMonitor.instance().updateInvestment(changedInvestment);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		BankMonitor.instance().updateInvestment(oldInvestment);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

}
