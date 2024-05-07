package applications.bank.gui.models;

import java.time.LocalDate;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import application.model.Money;
import applications.bank.model.Bank;
import applications.bank.model.Transaction;
import applications.bank.model.TransactionDetailsHandler;

public class SearchTransactionsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private static String[] COLUMNS = { "Date", "Bank", "Account", "Description", "Amount" };
	private static final int DATE = 0;
	private static final int BANK = 1;
	private static final int ACCOUNT = 2;
	private static final int DESCRIPTION = 3;
	private static final int AMOUNT = 4;

	private List<Transaction> transactions;

	public SearchTransactionsTableModel(List<Bank> banks, String search) {
		this.transactions = TransactionDetailsHandler.transactions(banks, search);
	}

	@Override
	public int getRowCount() {
		return transactions.size() + 1;
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
	public Class<?> getColumnClass(int col) {
		if (col == AMOUNT) {
			return Number.class;
		}
		return String.class;
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object value = "Unknown";
		if (row == transactions.size()) {
			value = closingBalance(col);
		} else {
			value = transactionRow(row, col);
		}
		return value;
	}

	private Object closingBalance(int col) {
		Object value = "Unknown";
		switch (col) {
			case DATE -> value = LocalDate.now().toString();
			case BANK -> value = "Total";
			case AMOUNT -> {
				Money total = new Money("0.00");
				for (Transaction t : transactions) {
					total = total.plus(t.amount());
				}
				value = total.cost();
			}
			default -> value = "";
		}
		return value;
	}

	private Object transactionRow(int row, int col) {
		Object value = "Unknown";
		Transaction transaction = transactions.get(row);
		switch (col) {
			case DATE -> value = transaction.date().toString();
			case BANK -> value = transaction.owner().owner().owner().name();
			case ACCOUNT -> value = transaction.owner().accountId().accountNumber();
			case DESCRIPTION -> value = transaction.description();
			case AMOUNT -> value = transaction.amount().cost();
			default -> value = "";
		}
		return value;
	}

}
