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
import applications.bank.model.Investment;
import applications.bank.storage.BankMonitor;

public class InvestmentTabbedPane extends ColoredTabbedPane {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = InvestmentTabbedPane.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private JPopupMenu popup;
	private BankActionFactory actionFactory;

	public InvestmentTabbedPane(BankApplicationMenu menuBar, IApplication application) {
		LOGGER.entering(CLASS_NAME, "init");
		this.addChangeListener((e) -> {
			tabSelectionChanged();
		});
		int i = 0;
		for (Investment investment : BankMonitor.instance().investments()) {
			addTab(investment.name(), new InvestmentPanel(investment, menuBar, application));
			if (ApplicationConfiguration.applicationDefinition().bottomColor().isPresent()) {
				setBackgroundAt(i, ApplicationConfiguration.applicationDefinition().bottomColor().get());
			}
			i++;
		}
		actionFactory = BankActionFactory.instance(application);
		popup = new JPopupMenu();
		JMenu viewMenu = new JMenu("View");
		popup.add(viewMenu);
		viewMenu.add(actionFactory.viewInvestmentPercentagesAction());
		LOGGER.exiting(CLASS_NAME, "init");
	}

	public InvestmentPanel selectedPanel() {
		LOGGER.entering(CLASS_NAME, "selectedPanel");
		InvestmentPanel selectedPanel = (InvestmentPanel) getSelectedComponent();
		LOGGER.exiting(CLASS_NAME, "selectedPanel", selectedPanel);
		return selectedPanel;
	}

	public Investment selectedInvestment() {
		LOGGER.entering(CLASS_NAME, "selectedInvestment");
		Investment selectedInvestment = selectedPanel().investment();
		LOGGER.exiting(CLASS_NAME, "selectedInvestment", selectedInvestment);
		return selectedInvestment;
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		for (int i = 0; i < getTabCount(); i++) {
			InvestmentPanel panel = (InvestmentPanel) getComponent(i);
			panel.removeListeners();
		}
		LOGGER.exiting(CLASS_NAME, "removeListeners");
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

}
