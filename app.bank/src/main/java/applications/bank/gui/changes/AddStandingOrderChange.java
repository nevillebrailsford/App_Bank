package applications.bank.gui.changes;

import java.util.logging.Logger;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import applications.bank.model.StandingOrder;
import applications.bank.storage.BankMonitor;

public class AddStandingOrderChange extends AbstractChange {
	private static final String CLASS_NAME = AddStandingOrderChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private StandingOrder standingOrder;

	public AddStandingOrderChange(StandingOrder standingOrder) {
		this.standingOrder = standingOrder;
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
		BankMonitor.instance().addStandingOrder(standingOrder);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		BankMonitor.instance().removeStandingOrder(standingOrder);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

}
