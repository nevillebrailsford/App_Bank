package applications.bank.gui.charts;

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
 * Create the pie chart to be placed in the SummaryPanel at the start of the
 * application.
 */
public class PieChartComponent extends ChartComponent implements TableModelListener {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = PieChartComponent.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	protected double[] percentages;

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
	 * Create the pie chart.
	 * 
	 * @param tm - the table model to be used as the model.
	 */
	public PieChartComponent(TableModel tm) {
		super(tm, new PieChartPainter());
		LOGGER.entering(CLASS_NAME, "init");
		addListeners();
		LOGGER.exiting(CLASS_NAME, "init");
	}

	protected void calculatePercentages() {
		LOGGER.entering(CLASS_NAME, "calculatePercentages");
		double runningTotal = 0.0;
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			percentages[i] = 0.0;
			for (int j = model.getColumnCount() - 1; j >= 0; j--) {
				Object val = model.getValueAt(i, j);
				if (val instanceof Number) {
					percentages[i] += ((Number) val).doubleValue();
				} else if (val instanceof String) {
					try {
						percentages[i] += Double.valueOf(val.toString()).doubleValue();
					} catch (Exception e) {
					}
				}
			}
			runningTotal += percentages[i];
		}
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			percentages[i] /= runningTotal;
		}
		LOGGER.exiting(CLASS_NAME, "calculatePercentages");
	}

	protected void createLabelsAndTips() {
		LOGGER.entering(CLASS_NAME, "createLabelsAndTips");
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			labels[i] = (String) model.getValueAt(i, 0);
			tips[i] = formatter.format(percentages[i]) + " (" + model.getValueAt(i, 1) + ")";
		}
		LOGGER.exiting(CLASS_NAME, "createLabelsAndTips");
	}

	@Override
	public void updateLocalValues(boolean freshStart) {
		LOGGER.entering(CLASS_NAME, "updateLocalValues", freshStart);
		int count = model.getRowCount();
		if (freshStart) {
			percentages = new double[count];
			labels = new String[count];
			tips = new String[count];
		} else {
			if (tips == null || count != tips.length) {
				percentages = new double[count];
				labels = new String[count];
				tips = new String[count];
			}
		}
		calculatePercentages();
		createLabelsAndTips();

		cp.setValues(percentages);
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
