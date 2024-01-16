package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.gui.IApplication;

public class AddStandingOrderAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public AddStandingOrderAction(IApplication application) {
		super("A New Standing Order");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.addStandingOrderAction();
	}

}
