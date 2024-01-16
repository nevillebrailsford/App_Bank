package applications.bank.gui.changes;

import java.util.logging.Logger;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Branch;
import applications.bank.storage.BankMonitor;

public class AddBranchChange extends AbstractChange {
	private static final String CLASS_NAME = AddBranchChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private Branch branch;

	public AddBranchChange(Branch branch) {
		this.branch = branch;
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
		BankMonitor.instance().addBranch(branch);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		BankMonitor.instance().removeBranch(branch);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

}
