package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.gui.IApplication;

public class RemoveInvestmentAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public RemoveInvestmentAction(IApplication application) {
		super("An Existing Investment");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.removeInvestmentAction();
	}

}
