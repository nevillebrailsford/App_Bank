package applications.bank.gui.charts;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import application.charting.ChartComponent;

public class LineChartComponent extends ChartComponent implements TableModelListener {
	private static final long serialVersionUID = 1L;

	protected double[] values;
	protected String[] dates;
	protected double divisor = 1d;

	public LineChartComponent(TableModel tm) {
		super(tm, new LineChartPainter());
		setUI(cp = new LineChartPainter());
		setModel(tm);
	}

	protected void createLabelsAndTips() {
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			tips[i] = String.valueOf(model.getValueAt(i, 1) + " on " + model.getValueAt(i, 0));
		}
	}

	protected void calculateValues() {
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
	}

	@Override
	public void updateLocalValues(boolean freshStart) {
		if (freshStart) {
			int count = model.getRowCount();
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
	}
}
