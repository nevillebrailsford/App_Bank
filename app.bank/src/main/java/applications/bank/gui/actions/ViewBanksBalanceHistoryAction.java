package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.application.IBankApplication;

public class ViewBanksBalanceHistoryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IBankApplication application;

	public ViewBanksBalanceHistoryAction(IBankApplication application) {
		super("All Banks Balance History");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.viewBanksBalanceHistoryAction();
	}

}
