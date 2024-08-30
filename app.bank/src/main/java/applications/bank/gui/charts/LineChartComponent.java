package applications.bank.gui.charts;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import application.charting.ChartComponent;
import application.definition.ApplicationConfiguration;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import applications.bank.gui.models.BankBalanceTableModel;
import applications.bank.storage.InvestmentNotificationType;
import applications.bank.storage.TransactionNotificationType;

/**
 * Create a line chart for any component.
 */
public class LineChartComponent extends ChartComponent implements TableModelListener {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = LineChartComponent.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	protected double[] values;
	protected String[] dates;
	protected double divisor = 1d;

	private NotificationListener transactionListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addRemoveNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "addChangeRemoveRun");
			updateModel();
			LOGGER.exiting(CLASS_NAME, "addChangeRemoveRun");
		});
		LOGGER.exiting(CLASS_NAME, "addRemoveNotify");
	};
	private NotificationListener investmentListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addRemoveNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "addChangeRemoveRun");
			updateModel();
			LOGGER.exiting(CLASS_NAME, "addChangeRemoveRun");
		});
		LOGGER.exiting(CLASS_NAME, "addRemoveNotify");
	};

	/**
	 * Create the line chart.
	 * 
	 * @param tm - the table model to be used as the model.
	 */
	public LineChartComponent(TableModel tm) {
		super(tm, new LineChartPainter());
		LOGGER.entering(CLASS_NAME, "init");
		setUI(cp = new LineChartPainter());
		setModel(tm);
		addListeners();
		LOGGER.exiting(CLASS_NAME, "init");
	}

	protected void createLabelsAndTips() {
		LOGGER.entering(CLASS_NAME, "createLabelsAndTips");
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			tips[i] = String.valueOf(model.getValueAt(i, 1) + " on " + model.getValueAt(i, 0));
		}
		LOGGER.exiting(CLASS_NAME, "createLabelsAndTips");
	}

	protected void calculateValues() {
		LOGGER.entering(CLASS_NAME, "calculateValues");
		int lSize = model.getRowCount();
		ArrayList<Double> vals = new ArrayList<>();
		for (int i = 0; i < lSize; i++) {
			vals.add(Double.parseDouble(((String) model.getValueAt(i, 1)).substring(1)));
		}
		if (vals.size() > 0) {
			double max = vals.stream().max(Double::compare).get();
			if (max > 100)
				divisor = 10;
			if (max > 1000)
				divisor = 100;
			if (max > 10000)
				divisor = 1000;
			if (max > 100000)
				divisor = 10000;
		}
		for (int i = 0; i < lSize; i++) {
			labels[i] = (String) model.getValueAt(i, 0);
			values[i] = Double.parseDouble(((String) model.getValueAt(i, 1)).substring(1));
		}
		LOGGER.exiting(CLASS_NAME, "calculateValues");
	}

	@Override
	public void updateLocalValues(boolean freshStart) {
		LOGGER.entering(CLASS_NAME, "updateLocalValues", freshStart);
		int count = model.getRowCount();
		if (freshStart) {
			values = new double[count];
			labels = new String[count];
			tips = new String[count];
		} else {
			if (tips == null || count != tips.length) {
				values = new double[count];
				labels = new String[count];
				tips = new String[count];
			}
		}
		calculateValues();
		createLabelsAndTips();

		cp.setValues(values);
		cp.setLabels(labels);

		repaint();
		LOGGER.exiting(CLASS_NAME, "updateLocalValues");
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "addListeners");
		NotificationCentre.addListener(investmentListener, InvestmentNotificationType.Add,
				InvestmentNotificationType.Changed, InvestmentNotificationType.Removed);
		NotificationCentre.addListener(transactionListener, TransactionNotificationType.Add,
				TransactionNotificationType.Removed);
		model.addTableModelListener(this);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	private void updateModel() {
		LOGGER.entering(CLASS_NAME, "updateModel");
		model.removeTableModelListener(this);
		BankBalanceTableModel newModel = new BankBalanceTableModel();
		setModel(newModel);
		newModel.addTableModelListener(this);
		LOGGER.exiting(CLASS_NAME, "updateModel");
	}
}
