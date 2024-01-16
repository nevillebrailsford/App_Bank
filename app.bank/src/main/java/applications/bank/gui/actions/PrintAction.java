package applications.bank.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import applications.bank.gui.IApplication;

public class PrintAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public PrintAction(IApplication application) {
		super("Print Report");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.printAction();
	}

}
