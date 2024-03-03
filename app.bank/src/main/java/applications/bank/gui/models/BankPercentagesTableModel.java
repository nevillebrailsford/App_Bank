package applications.bank.gui.models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import application.model.Money;
import applications.bank.model.Bank;
import applications.bank.model.TransactionDetailsHandler;
import applications.bank.storage.BankMonitor;

public class BankPercentagesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static String[] COLUMNS = { "Bank", "Balance" };

	List<Bank> banks;

	public BankPercentagesTableModel() {
		banks = BankMonitor.instance().banks();
	}

	@Override
	public int getRowCount() {
		return banks.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			return banks.get(row).name();
		} else {
			return Money.sum(TransactionDetailsHandler.balance(banks.get(row))).toString();
			// banks.get(row).balance().toString();
		}
	}

}
