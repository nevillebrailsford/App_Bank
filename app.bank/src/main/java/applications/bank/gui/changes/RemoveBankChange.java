package applications.bank.gui.changes;

import java.util.logging.Logger;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Bank;
import applications.bank.storage.BankMonitor;

public class RemoveBankChange extends AbstractChange {
	private static final String CLASS_NAME = RemoveBankChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Bank bank;

	public RemoveBankChange(Bank bank) {
		this.bank = bank;
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
		BankMonitor.instance().removeBank(bank);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		BankMonitor.instance().addBank(bank);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

}
