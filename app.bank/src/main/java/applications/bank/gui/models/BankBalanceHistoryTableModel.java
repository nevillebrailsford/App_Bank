package applications.bank.gui.models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import application.model.Money;
import applications.bank.model.Bank;
import applications.bank.model.Transaction;

public class BankBalanceHistoryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private Bank bank = null;
	private List<Transaction> transactions = null;
	private static String[] COLUMNS = { "Date", "Balance" };
	private static final int DATE = 0;
	private static final int BALANCE = 1;

	public BankBalanceHistoryTableModel(Bank bank) {
		this.bank = bank;
		if (bank != null) {
			// this.transactions = bank.transactions();
		}
	}

	@Override
	public int getRowCount() {
		if (transactions == null) {
			return 0;
		}
		return transactions.size();
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
		Transaction t = transactions.get(row);
		Object value = "Unknown";
		switch (col) {
			case DATE:
				value = t.date().toString();
				break;
			case BALANCE:
				value = calulateBalance(t).cost();
				break;
		}
		return value;
	}

	private Money calulateBalance(Transaction transaction) {
		return bank.balance(transaction.date());
	}

}
