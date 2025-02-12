package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.application.IBankApplication;

public class ReactivateAccountAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IBankApplication application;

	public ReactivateAccountAction(IBankApplication application) {
		super("Reactivate Account");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.reactivateAccount();
	}

}
