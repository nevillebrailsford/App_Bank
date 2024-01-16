package applications.bank.gui.changes;

import java.util.logging.Logger;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Transfer;
import applications.bank.storage.BankMonitor;

public class TransferChange extends AbstractChange {
	private static final String CLASS_NAME = TransferChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Transfer transfer;

	public TransferChange(Transfer transfer) {
		this.transfer = transfer;
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
		BankMonitor.instance().transfer(transfer);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		Transfer reverse = Transfer.reverse(transfer);
		BankMonitor.instance().transfer(reverse);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

}
