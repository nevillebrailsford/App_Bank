package applications.bank.gui.charts;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class LineChartTester extends JFrame {
	private static final long serialVersionUID = 1L;

	public LineChartTester() {
		super("Simple JTable Test");
		setSize(300, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		TableModel tm = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			String data[][] = { { "2021-08-27", "£120000.97" }, { "2021-09-03", "£122374.58" },
					{ "2021-09-10", "£122497.84" }, { "2021=09-17", "£123576.37" }, { "2021-10-08", "£119163.53" } };
			String[] headers = new String[] { "Date", "Value" };

			@Override
			public int getColumnCount() {
				return headers.length;
			}

			@Override
			public int getRowCount() {
				return data.length;
			}

			@Override
			public String getColumnName(int col) {
				return headers[col];
			}

			@Override
			public Class<?> getColumnClass(int col) {
				return col == 0 ? String.class : Number.class;
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return true;
			}

			@Override
			public void setValueAt(Object value, int row, int col) {
				data[row][col] = (String) value;
				fireTableRowsUpdated(row, row);
			}

			@Override
			public Object getValueAt(int row, int col) {
				return data[row][col];
			}
		};

		JTable jt = new JTable(tm);
		JScrollPane jsp = new JScrollPane(jt);
		getContentPane().add(jsp, BorderLayout.CENTER);

		final LineChartPopupT tcp = new LineChartPopupT(tm, "Table Line Chart");
		JButton button = new JButton("Show me a line chart of this table");
		button.addActionListener((e) -> {
			tcp.setVisible(true);
		});
		getContentPane().add(button, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		LineChartTester ct = new LineChartTester();
		ct.setVisible(true);
	}
}
