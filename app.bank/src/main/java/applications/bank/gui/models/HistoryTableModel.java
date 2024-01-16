package applications.bank.gui.models;

import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import application.definition.ApplicationConfiguration;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import applications.bank.model.Investment;
import applications.bank.model.Investment.ValueOn;
import applications.bank.storage.InvestmentNotificationType;

public class HistoryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = HistoryTableModel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private static String[] COLUMNS = { "Date", "Value" };
	private static final int DATE = 0;
	private static final int VALUE = 1;

	private Investment investment;
	private List<ValueOn> values;

	private NotificationListener changeNotificationListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "changeNotify");
		Investment investment = (Investment) notification.subject().get();
		if (this.investment.equals(investment)) {
			SwingUtilities.invokeLater(() -> {
				changeInvestmentNotify(investment);
			});
		}
		LOGGER.exiting(CLASS_NAME, "changeNotify");
	};

	public HistoryTableModel(Investment investment) {
		LOGGER.entering(CLASS_NAME, "init");
		this.investment = investment;
		this.values = investment.history();
		addListeners();
		LOGGER.exiting(CLASS_NAME, "init");
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
	public Object getValueAt(int row, int col) {
		ValueOn valueOn = values.get(row);
		Object value = "Unknown";
		switch (col) {
			case DATE:
				value = valueOn.date().toString();
				break;
			case VALUE:
				value = valueOn.value().cost();
				break;
		}
		return value;
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "addListeners");
		NotificationCentre.addListener(changeNotificationListener, InvestmentNotificationType.Add);
		NotificationCentre.addListener(changeNotificationListener, InvestmentNotificationType.Changed);
		NotificationCentre.addListener(changeNotificationListener, InvestmentNotificationType.Removed);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	private void changeInvestmentNotify(Investment investment) {
		values.removeAll(values);
		this.investment = investment;
		values = investment.history();
		fireTableDataChanged();
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		NotificationCentre.removeListener(changeNotificationListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

}
