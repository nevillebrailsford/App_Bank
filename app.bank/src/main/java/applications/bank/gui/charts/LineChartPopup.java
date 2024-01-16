package applications.bank.gui.charts;

import javax.swing.table.TableModel;

import application.charting.ChartPopup;

public class LineChartPopup extends ChartPopup {
	private static final long serialVersionUID = 1L;

	public LineChartPopup(TableModel tm, String title) {
		super(new LineChartComponent(tm), tm, title);
		setSize(500, 400);
	}
}
