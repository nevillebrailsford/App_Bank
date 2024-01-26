package applications.bank.gui.modified;

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import application.base.app.gui.ColoredTabbedPane;
import application.definition.ApplicationConfiguration;
import applications.bank.gui.BankApplicationMenu;
import applications.bank.gui.IApplication;
import applications.bank.gui.actions.BankActionFactory;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.storage.BankMonitor;

public class BankTabbedPane extends ColoredTabbedPane {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = BankTabbedPane.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();
	private JPopupMenu popup;
	private BankActionFactory actionFactory;

	public BankTabbedPane(BankApplicationMenu menuBar, IApplication application) {
		LOGGER.entering(CLASS_NAME, "init");
		int i = 0;
		for (Bank bank : BankMonitor.instance().banks()) {
			addTab(bank.name(), new BankPanel(bank, menuBar, application));
			if (ApplicationConfiguration.applicationDefinition().bottomColor().isPresent()) {
				setBackgroundAt(i, ApplicationConfiguration.applicationDefinition().bottomColor().get());
			}
			i++;
		}
		this.addChangeListener((e) -> {
			tabSelectionChanged();
		});
		actionFactory = BankActionFactory.instance(application);
		popup = new JPopupMenu();
		JMenu addMenu = new JMenu("Add");
		popup.add(addMenu);
		addMenu.add(actionFactory.addBankAction());
		addMenu.add(actionFactory.addAccountAction());
		addMenu.add(actionFactory.addStandingOrderAction());
		JMenu removeMenu = new JMenu("Remove");
		popup.add(removeMenu);
		removeMenu.add(actionFactory.removeBankAction());
		removeMenu.add(actionFactory.removeAccountAction());
		removeMenu.add(actionFactory.removeStandingOrderAction());
		JMenu viewMenu = new JMenu("View");
		popup.add(viewMenu);
		viewMenu.add(actionFactory.viewBankPercentagesAction());
		LOGGER.exiting(CLASS_NAME, "init");
	}

	public BankPanel selectedPanel() {
		LOGGER.entering(CLASS_NAME, "selectedPanel");
		BankPanel selectedPanel = (BankPanel) getSelectedComponent();
		LOGGER.exiting(CLASS_NAME, "selectedPanel", selectedPanel);
		return selectedPanel;
	}

	public Account selectedAccount() {
		LOGGER.entering(CLASS_NAME, "selectedAccount");
		Account account = selectedPanel().selectedAccount();
		LOGGER.exiting(CLASS_NAME, "selectedAccount", account);
		return account;
	}

	private void tabSelectionChanged() {
		LOGGER.entering(CLASS_NAME, "tabSelectionChanged");
		if (selectedPanel() != null) {
			selectedPanel().tabSelectionChanged();
		}
		LOGGER.exiting(CLASS_NAME, "tabSelectionChanged");
	}

	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(this, e.getX(), e.getY());
		} else {
			super.processMouseEvent(e);
		}
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		for (int i = 0; i < getTabCount(); i++) {
			BankPanel panel = (BankPanel) getComponent(i);
			panel.removeListeners();
		}
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

}
