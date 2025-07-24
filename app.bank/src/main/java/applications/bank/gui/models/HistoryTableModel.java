package applications.bank.gui.models;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import application.definition.ApplicationConfiguration;
import application.model.Money;
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

	public static final int ASC = 1;
	public static final int DESC = 2;
	
	public static final boolean CHART = true;

	private Investment investment;
	private List<ValueOn> values;
	private int order;
	private boolean isChart = false;

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

	public HistoryTableModel(Investment investment, int order, boolean isChart) {
		LOGGER.entering(CLASS_NAME, "init", new Object[] { order, isChart });
		this.order = order;
		this.isChart = isChart;
		this.investment = investment;
		this.values = investment.history();
		if (order == DESC) {
			Collections.reverse(this.values);
		}
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
	public Class<?> getColumnClass(int col) {
		if (col == DATE) {
			if (isChart) {
				return String.class;
			} else {
				return LocalDate.class;
			}
		}
		if (col == VALUE) {
			if (isChart) {
				return String.class;
			} else {
				return Money.class;
			}
		}
		return String.class;
	}
	@Override
	public Object getValueAt(int row, int col) {
		ValueOn valueOn = values.get(row);
		Object value = "Unknown";
		switch (col) {
			case DATE:
				if (isChart) {
					value = valueOn.date().toString();
				} else {
					value = valueOn.date();
				}
				break;
			case VALUE:
				if (isChart) {
					value = valueOn.value().cost().replace(",", "");
				} else {
					value = valueOn.value();
				}
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
		if (order == DESC) {
			Collections.reverse(values);
		}
		fireTableDataChanged();
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		NotificationCentre.removeListener(changeNotificationListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

}
