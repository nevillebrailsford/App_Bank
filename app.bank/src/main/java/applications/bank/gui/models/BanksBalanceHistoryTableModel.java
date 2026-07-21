package applications.bank.gui.models;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.logging.Logger;

import application.definition.ApplicationConfiguration;

public class BanksBalanceHistoryTableModel extends BaseTableModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = BanksBalanceHistoryTableModel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	public BanksBalanceHistoryTableModel() {
		super();
		LOGGER.entering(CLASS_NAME, "init");
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	protected void collectDates() {
		LOGGER.entering(CLASS_NAME, "collectDates");
		dates = new HashSet<>();
		collectTransactionDates();
		LOGGER.exiting(CLASS_NAME, "collectDates");
	}

	@Override
	protected void calculateValues() {
		LOGGER.entering(CLASS_NAME, "calculateValues");
		values = new TreeMap<>();
		calculateTransactionValues();
		LOGGER.exiting(CLASS_NAME, "calculateValues");
	}

}
