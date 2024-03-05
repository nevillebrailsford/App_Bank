package applications.bank.gui.models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import application.model.Money;
import applications.bank.model.Bank;
import applications.bank.model.Transaction;
import applications.bank.model.TransactionDetailsHandler;

public class BanksBalanceHistoryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private List<Bank> banks = null;
	private Transaction[] transactions = null;
	private static String[] COLUMNS = { "Date", "Balance" };
	private static final int DATE = 0;
	private static final int BALANCE = 1;

	public BanksBalanceHistoryTableModel(List<Bank> banks) {
		this.banks = banks;
		if (banks != null) {
			this.transactions = TransactionDetailsHandler.transactions(banks);
		}
	}

	@Override
	public int getRowCount() {
		if (transactions == null) {
			return 0;
		}
		return transactions.length;
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Object getValueAt(int row, int col) {
		Transaction t = transactions[row];
		Object value = "Unknown";
		switch (col) {
			case DATE:
				value = t.date().toString();
				break;
			case BALANCE:
				value = Money.sum(TransactionDetailsHandler.balance(banks, t.date())).cost();
				break;
		}
		return value;
	}

}
