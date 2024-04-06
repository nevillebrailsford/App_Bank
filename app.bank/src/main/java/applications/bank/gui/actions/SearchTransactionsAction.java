package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.gui.IApplication;

public class SearchTransactionsAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public SearchTransactionsAction(IApplication application) {
		super("Transactions");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.searchTransactionsAction();
	}

}
