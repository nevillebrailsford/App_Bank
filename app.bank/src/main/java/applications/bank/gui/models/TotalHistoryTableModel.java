package applications.bank.gui.models;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.logging.Logger;

import application.definition.ApplicationConfiguration;

public class TotalHistoryTableModel extends BaseTableModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = TotalHistoryTableModel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	public TotalHistoryTableModel() {
		super();
		LOGGER.entering(CLASS_NAME, "init");
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	protected void collectDates() {
		LOGGER.entering(CLASS_NAME, "collectDates");
		dates = new HashSet<>();
		collectInvestmentDates();
		LOGGER.exiting(CLASS_NAME, "collectDates");
	}

	@Override
	protected void calculateValues() {
		LOGGER.entering(CLASS_NAME, "calculateValues");
		values = new TreeMap<>();
		calculateInvestmentValues();
		LOGGER.exiting(CLASS_NAME, "calculateValues");
	}

}
