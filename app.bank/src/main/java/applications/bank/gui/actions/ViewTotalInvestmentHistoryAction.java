package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.application.IBankApplication;

public class ViewTotalInvestmentHistoryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IBankApplication application;

	public ViewTotalInvestmentHistoryAction(IBankApplication application) {
		super("Total Investment History");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.viewTotalInvestmentHistoryAction();
	}

}
