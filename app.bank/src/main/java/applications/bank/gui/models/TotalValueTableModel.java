package applications.bank.gui.models;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.logging.Logger;

import application.definition.ApplicationConfiguration;

/**
 * Model for line chart showing total values.
 */
public class TotalValueTableModel extends BaseTableModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = TotalValueTableModel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	/**
	 * Create the table model.
	 */
	public TotalValueTableModel() {
		super();
		LOGGER.entering(CLASS_NAME, "init");
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	protected void collectDates() {
		LOGGER.entering(CLASS_NAME, "collectDates");
		dates = new HashSet<>();
		collectInvestmentDates();
		collectTransactionDates();
		LOGGER.exiting(CLASS_NAME, "collectDates");
	}

	@Override
	protected void calculateValues() {
		LOGGER.entering(CLASS_NAME, "calculateValues");
		values = new TreeMap<>();
		calculateInvestmentValues();
		calculateTransactionValues();
		LOGGER.exiting(CLASS_NAME, "calculateValues");
	}

}
