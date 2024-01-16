package applications.bank.gui.charts;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class PieChartTester extends JFrame {
	private static final long serialVersionUID = 1L;

	public PieChartTester() {
		super("Simple JTable Test");
		setSize(300, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		TableModel tm = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			String data[][] = { { " Ron", "0.00", "68.68", "77.34", "78.02" },
					{ " Ravi", "0.00", "70.89", "64.17", "75.00" }, { " Maria", "76.52", "71.12", "75.68", "74.14" },
					{ " James", "70.00", "15.72", "26.40", "38.32" },
					{ " Ellen", "80.32", "78.16", "83.80", "85.72" } };
			String[] headers = new String[] { "", "Q1", "Q2", "Q3", "Q4" };

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

		final PieChartPopupT tcp = new PieChartPopupT(tm, "Table Pie Chart");
		JButton button = new JButton("Show me a chart of this table");
		button.addActionListener((e) -> {
			tcp.setVisible(true);
		});
		getContentPane().add(button, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		PieChartTester ct = new PieChartTester();
		ct.setVisible(true);
	}
}
