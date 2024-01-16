package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.gui.IApplication;

public class AddBankAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public AddBankAction(IApplication application) {
		super("A New Bank");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.addBankAction();
	}

}
