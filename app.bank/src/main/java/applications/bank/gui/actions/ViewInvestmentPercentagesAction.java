package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.gui.IApplication;

public class ViewInvestmentPercentagesAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public ViewInvestmentPercentagesAction(IApplication application) {
		super("Investment Distribution");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.viewInvestmentPercentagesAction();
	}

}
