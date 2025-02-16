package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.application.IBankApplication;

public class PrintCategorySummaryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IBankApplication application;

	public PrintCategorySummaryAction(IBankApplication application) {
		super("Print Category Summary");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.printCategorySummaryAction();
	}

}
