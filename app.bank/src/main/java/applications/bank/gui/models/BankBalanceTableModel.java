package applications.bank.gui.models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import application.model.Money;
import applications.bank.model.Bank;
import applications.bank.model.Investment;
import applications.bank.storage.BankMonitor;

public class BankBalanceTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static String[] COLUMNS = { "Bank", "Balance" };

	List<Bank> banks;
	List<Investment> investments;

	public BankBalanceTableModel() {
		banks = BankMonitor.instance().banks();
		investments = BankMonitor.instance().investments();
	}

	@Override
	public int getRowCount() {
		return 2;
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public Object getValueAt(int row, int col) {

		if (col == 0) {
			if (row == 0) {
				return "Banks";
			} else {
				return "Investments";
			}
		} else {
			if (row == 0) {
				Money balance = new Money("0.00");
				for (Bank bank : banks) {
					balance = balance.plus(bank.balance());
				}
				return balance.toString();
			} else {
				Money balance = new Money("0.00");
				for (Investment investment : investments) {
					balance = balance.plus(investment.value());
				}
				return balance.toString();
			}
		}
	}

}
