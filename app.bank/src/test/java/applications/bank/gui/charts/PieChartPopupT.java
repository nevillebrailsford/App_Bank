package applications.bank.gui.charts;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.table.TableModel;

public class PieChartPopupT extends JDialog {
	private static final long serialVersionUID = 1L;

	public PieChartPopupT(TableModel tm, String title) {
		setTitle(title);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setSize(500, 400);
		PieChartComponent tc = new PieChartComponent(tm);
		getContentPane().add(tc, BorderLayout.CENTER);
		ToolTipManager.sharedInstance().registerComponent(tc);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

}
