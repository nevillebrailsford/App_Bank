package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.application.IBankApplication;

public class PrintTaxDetailsForYearAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IBankApplication application;

	public PrintTaxDetailsForYearAction(IBankApplication application) {
		super("Print Tax Report");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.printTaxAction();
	}

}
