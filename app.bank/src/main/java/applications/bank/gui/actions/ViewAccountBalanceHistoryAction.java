package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.application.IBankApplication;

public class ViewAccountBalanceHistoryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IBankApplication application;

	public ViewAccountBalanceHistoryAction(IBankApplication application) {
		super("Account Balance History");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.viewAccountBalanceHistoryAction();
	}

}
