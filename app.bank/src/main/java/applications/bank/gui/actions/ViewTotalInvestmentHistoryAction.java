package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.gui.IApplication;

public class ViewTotalInvestmentHistoryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public ViewTotalInvestmentHistoryAction(IApplication application) {
		super("Total Investment History");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.viewTotalInvestmentHistoryAction();
	}

}
