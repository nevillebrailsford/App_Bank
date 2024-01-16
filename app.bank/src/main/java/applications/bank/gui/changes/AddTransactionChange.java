package applications.bank.gui.changes;

import java.util.logging.Logger;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Transaction;
import applications.bank.storage.BankMonitor;

public class AddTransactionChange extends AbstractChange {
	private static final String CLASS_NAME = AddTransactionChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Transaction transaction;

	public AddTransactionChange(Transaction transaction) {
		this.transaction = transaction;
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
		BankMonitor.instance().addTransaction(transaction);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		BankMonitor.instance().removeTransaction(transaction);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

}
