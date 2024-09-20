package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.application.IBankApplication;

public class ViewBankPercentagesAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IBankApplication application;

	public ViewBankPercentagesAction(IBankApplication application) {
		super("Bank Fund Distribution");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.viewBankPercentagesAction();
	}

}
