package applications.bank.gui.models;

import java.time.LocalDate;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import applications.bank.model.Investment;
import applications.bank.storage.BankMonitor;

public class InvestmentPercentagesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static String[] COLUMNS = { "Investment", "Balance" };

	List<Investment> investments;

	public InvestmentPercentagesTableModel() {
		investments = BankMonitor.instance().investments();
	}

	public InvestmentPercentagesTableModel(List<Investment> investments) {
		this.investments = investments;
	}

	@Override
	public int getRowCount() {
		return investments.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return investments.get(row).name();
		} else {
			return investments.get(row).value(LocalDate.now()).toString();
		}
	}

	public double maxValue() {
		long test = investments.stream().map(investment -> investment.history()).count();
		return test;
	}

}
