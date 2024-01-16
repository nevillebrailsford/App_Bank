package applications.bank.gui.changes;

import java.util.logging.Logger;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import applications.bank.model.StandingOrder;
import applications.bank.storage.BankMonitor;

public class ChangeStandingOrderChange extends AbstractChange {
	private static final String CLASS_NAME = ChangeStandingOrderChange.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	private StandingOrder oldStandingOrder;
	private StandingOrder changedStandingOrder;

	public ChangeStandingOrderChange(StandingOrder oldStandingOrder, StandingOrder changedStandinginOrder) {
		this.oldStandingOrder = oldStandingOrder;
		this.changedStandingOrder = changedStandinginOrder;
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
		BankMonitor.instance().updateStandingOrder(changedStandingOrder);
		LOGGER.exiting(CLASS_NAME, "redoHook");
	}

	@Override
	protected void undoHook() throws Failure {
		LOGGER.entering(CLASS_NAME, "undoHook");
		BankMonitor.instance().updateStandingOrder(oldStandingOrder);
		LOGGER.exiting(CLASS_NAME, "undoHook");
	}

}
