package applications.bank.gui.models;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import application.definition.ApplicationConfiguration;
import application.model.Money;
import application.utils.Util;
import applications.bank.model.Investment;
import applications.bank.model.Investment.ValueOn;
import applications.bank.model.InvestmentHistoryHandler;
import applications.bank.model.Transaction;
import applications.bank.model.TransactionDetailsHandler;
import applications.bank.storage.BankMonitor;

public abstract class BaseTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = BaseTableModel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private static String[] COLUMNS = { "Date", "Value" };
	private static final int DATE = 0;
	private static final int VALUE = 1;

	protected Set<LocalDate> dates;
	protected Map<LocalDate, Money> values;

	public BaseTableModel() {
		LOGGER.entering(CLASS_NAME, "init");
		refresh();
		LOGGER.exiting(CLASS_NAME, "init");
	}

	/**
	 * Refresh the values in the table.
	 */
	protected void refresh() {
		LOGGER.entering(CLASS_NAME, "refresh");
		collectDates();
		calculateValues();
		LOGGER.exiting(CLASS_NAME, "refresh");
	}

	protected abstract void collectDates();

	protected abstract void calculateValues();

	protected void collectInvestmentDates() {
		LOGGER.entering(CLASS_NAME, "collectInvestmentDates");
		Investment[] investments = BankMonitor.instance().investments().toArray(new Investment[] {});
		for (int i = 0; i < investments.length; i++) {
			ValueOn[] valuesOn = investments[i].history().toArray(new ValueOn[] {});
			for (int j = 0; j < valuesOn.length; j++) {
				dates.add(valuesOn[j].date());
			}
		}
		LOGGER.exiting(CLASS_NAME, "collectInvestmentDates");
	}

	protected void collectTransactionDates() {
		LOGGER.entering(CLASS_NAME, "collectTransactionDates");
		Transaction[] transactions = TransactionDetailsHandler.transactions(BankMonitor.instance().banks());
		for (int i = 0; i < transactions.length; i++) {
			dates.add(transactions[i].date());
		}
		LOGGER.exiting(CLASS_NAME, "collectTransactionDates");
	}

	protected void calculateInvestmentValues() {
		LOGGER.entering(CLASS_NAME, "calculateInvestmentValues");
		dates.forEach((d) -> {
			Money total = InvestmentHistoryHandler.value(BankMonitor.instance().investments(), d);
			if (values.containsKey(d)) {
				total = total.plus(values.get(d));
			}
			values.put(d, total);
		});
		LOGGER.exiting(CLASS_NAME, "calculateInvestmentValues");
	}

	protected void calculateTransactionValues() {
		LOGGER.entering(CLASS_NAME, "calculateTransactionValues");
		dates.forEach((d) -> {
			Money total = Money.sum(TransactionDetailsHandler.balance(BankMonitor.instance().banks(), d));
			if (values.containsKey(d)) {
				total = total.plus(values.get(d));
			}
			values.put(d, total);
		});
		LOGGER.exiting(CLASS_NAME, "calculateTransactionValues");
	}

	@Override
	public int getRowCount() {
		return values.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = "Unknown";
		switch (columnIndex) {
			case DATE:
				LocalDate d = (LocalDate) values.keySet().toArray()[rowIndex];
				result = Util.displayDate(d);
				break;
			case VALUE:
				LocalDate k = (LocalDate) values.keySet().toArray()[rowIndex];
				Money m = values.get(k);
				result = m.cost().replace(",", "");
				break;
		}
		return result;
	}

}
