package applications.bank.gui.charts;

import javax.swing.table.TableModel;

import application.charting.ChartPopup;

public class PieChartPopup extends ChartPopup {
	private static final long serialVersionUID = 1L;

	public PieChartPopup(TableModel tm, String title) {
		super(new PieChartComponent(tm), tm, title);
		setSize(500, 400);
	}
}
