package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.gui.IApplication;

public class AddAccountAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public AddAccountAction(IApplication application) {
		super("A New Account");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.addAccountAction();
	}

}
