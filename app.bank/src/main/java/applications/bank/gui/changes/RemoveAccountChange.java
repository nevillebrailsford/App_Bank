package applications.bank.gui.changes;

import java.util.logging.Logger;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Account;
import applications.bank.storage.BankMonitor;

public class RemoveAccountChange extends AbstractChange {
	private static final String CLASS_NAME = RemoveAccountChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Account account;

	public RemoveAccountChange(Account account) {
		this.account = account;
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
		BankMonitor.instance().removeAccount(account);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		BankMonitor.instance().addAccount(account);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

}
