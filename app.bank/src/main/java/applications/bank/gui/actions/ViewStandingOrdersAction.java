package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.application.IBankApplication;

public class ViewStandingOrdersAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IBankApplication application;

	public ViewStandingOrdersAction(IBankApplication application) {
		super("Standing Orders");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.viewStandingOrdersAction();
	}

}
