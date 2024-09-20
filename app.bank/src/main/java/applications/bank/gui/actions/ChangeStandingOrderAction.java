package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.application.IBankApplication;

public class ChangeStandingOrderAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IBankApplication application;

	public ChangeStandingOrderAction(IBankApplication application) {
		super("An Existing Standing Order");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.changeStandingOrderAction();
	}

}
