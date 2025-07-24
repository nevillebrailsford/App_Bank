package applications.bank.gui.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import application.definition.ApplicationConfiguration;
import application.model.Money;
import applications.bank.model.Investment;
import applications.bank.model.Investment.ValueOn;

public class TotalHistoryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = TotalHistoryTableModel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private static String[] COLUMNS = { "Date", "Value" };
	private static final int DATE = 0;
	private static final int VALUE = 1;

	private List<Investment> investments;
	private Map<LocalDate, Double[]> mapValues;
	private List<ValueOn> values = new ArrayList<>();;
	private int investmentCount;
	private double[] currentArray;

	public TotalHistoryTableModel(List<Investment> investments) {
		LOGGER.entering(CLASS_NAME, "init");
		this.investments = investments;
		loadMapValues();
		createCurrentArray();
		loadValues();
		LOGGER.exiting(CLASS_NAME, "init");
	}

	private void loadMapValues() {
		LOGGER.entering(CLASS_NAME, "loadMapValues");
		mapValues = new HashMap<>();
		investmentCount = investments.size();
		int currentInvestment = 0;
		for (Investment investment : investments) {
			List<ValueOn> valueOns = investment.history();
			for (ValueOn valueOn : valueOns) {
				Double[] aValues = mapValues.get(valueOn.date());
				if (aValues == null) {
					aValues = new Double[investmentCount];
					for (int i = 0; i < aValues.length; i++) {
						aValues[i] = null;
					}
					mapValues.put(valueOn.date(), aValues);
				}
				aValues[currentInvestment] = Double.valueOf(valueOn.value().toString());
			}
			currentInvestment++;
		}
		LOGGER.exiting(CLASS_NAME, "loadMapValues");
	}

	private void createCurrentArray() {
		LOGGER.entering(CLASS_NAME, "createCurrentArray");
		currentArray = new double[investmentCount];
		for (int i = 0; i < currentArray.length; i++) {
			currentArray[i] = 0.0d;
		}
		LOGGER.exiting(CLASS_NAME, "createCurrentArray");
	}

	private void loadValues() {
		LOGGER.entering(CLASS_NAME, "loadValues");
		List<LocalDate> dates = mapValues.keySet().stream().collect(Collectors.toList());
		Collections.sort(dates);
		for (LocalDate date : dates) {
			Double[] vals = mapValues.get(date);
			for (int i = 0; i < vals.length; i++) {
				if (vals[i] != null) {
					currentArray[i] = vals[i];
				}
			}
			double amount = 0.0d;
			for (int i = 0; i < currentArray.length; i++) {
				amount += currentArray[i];
			}
			ValueOn newValueOn = new ValueOn(date, new Money(String.valueOf(amount)));
			values.add(newValueOn);
		}
		Collections.sort(values);
		LOGGER.exiting(CLASS_NAME, "loadValues");
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
		Object value = "Unknown";
		if (getRowCount() > 0) {
			ValueOn valueOn = values.get(row);
			switch (col) {
				case DATE:
					value = valueOn.date().toString();
					break;
				case VALUE:
					value = valueOn.value().cost().replace(",", "");
					break;
			}
		}
		return value;
	}

}
