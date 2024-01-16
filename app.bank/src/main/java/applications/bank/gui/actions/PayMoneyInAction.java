package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.gui.IApplication;

public class PayMoneyInAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public PayMoneyInAction(IApplication application) {
		super("Pay Money in");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.payMoneyInAction();
	}

}
