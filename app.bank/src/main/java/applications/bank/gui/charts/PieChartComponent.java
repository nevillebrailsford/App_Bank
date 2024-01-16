package applications.bank.gui.charts;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import application.charting.ChartComponent;

public class PieChartComponent extends ChartComponent implements TableModelListener {
	private static final long serialVersionUID = 1L;

	protected double[] percentages;

	public PieChartComponent(TableModel tm) {
		super(tm, new PieChartPainter());
	}

	protected void calculatePercentages() {
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
	}

	protected void createLabelsAndTips() {
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			labels[i] = (String) model.getValueAt(i, 0);
			tips[i] = formatter.format(percentages[i]) + " (" + model.getValueAt(i, 1) + ")";
		}
	}

	@Override
	public void updateLocalValues(boolean freshStart) {
		if (freshStart) {
			int count = model.getRowCount();
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
	}
}
