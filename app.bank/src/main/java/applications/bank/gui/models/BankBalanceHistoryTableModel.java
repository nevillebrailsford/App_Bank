package applications.bank.gui.models;

import java.time.LocalDate;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import application.model.Money;
import applications.bank.model.Account;
import applications.bank.model.Transaction;

public class BankBalanceHistoryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private Account account = null;
	private List<Transaction> transactions = null;
	private static String[] COLUMNS = { "Date", "Balance" };
	private static final int DATE = 0;
	private static final int BALANCE = 1;

	public BankBalanceHistoryTableModel(Account account) {
		this.account = account;
		this.transactions = account.transactions();
	}

	@Override
	public int getRowCount() {
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
		Transaction t = account.transactions().get(row);
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
		Money result = new Money("0.00");
		LocalDate endDate = transaction.date();
		for (int i = 0; i < transactions.size(); i++) {
			Transaction t = transactions.get(i);
			if (t.date().isAfter(endDate)) {
				break;
			}
			result = result.plus(t.amount());
		}
		return result;
	}

}
