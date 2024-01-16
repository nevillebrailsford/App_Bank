package applications.bank.gui.modified;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import applications.bank.gui.BankApplicationMenu;
import applications.bank.gui.IApplication;
import applications.bank.gui.actions.BankActionFactory;
import applications.bank.model.Investment;
import applications.bank.storage.InvestmentNotificationType;

public class InvestmentPanel extends ColoredPanel {
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = InvestmentPanel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	private Investment investment;
	private BankApplicationMenu menuBar;
	private BankActionFactory actionFactory;
	private HistoryPanel historyPanel;
	private JButton exit;

	private NotificationListener changeListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addNotify", notification);
		SwingUtilities.invokeLater(() -> {
			updateMenuItems();
		});
		LOGGER.exiting(CLASS_NAME, "addNotify");
	};

	public InvestmentPanel(Investment investment, BankApplicationMenu menuBar, IApplication application) {
		LOGGER.entering(CLASS_NAME, "init", investment);
		this.investment = investment;
		actionFactory = BankActionFactory.instance(application);
		setLayout(new BorderLayout());
		this.menuBar = menuBar;
		JPanel buttonPanel = new BottomColoredPanel();
		historyPanel = new HistoryPanel(investment, menuBar, application);
		add(historyPanel, BorderLayout.CENTER);
		exit = new JButton(actionFactory.exitAction());
		buttonPanel.add(exit);
		addListeners();
	}

	public void tabSelectionChanged() {
		LOGGER.entering(CLASS_NAME, "tabSelectionChanged");
		updateMenuItems();
		LOGGER.exiting(CLASS_NAME, "tabSelectionChanged");
	}

	public boolean owns(Investment investment) {
		return this.investment.equals(investment);
	}

	public Investment investment() {
		return investment;
	}

	private void updateMenuItems() {
		menuBar.enableRedo(ChangeManager.instance().redoable());
		menuBar.enableUndo(ChangeManager.instance().undoable());
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "addListeners");
		NotificationCentre.addListener(changeListener, InvestmentNotificationType.Add);
		NotificationCentre.addListener(changeListener, InvestmentNotificationType.Changed);
		NotificationCentre.addListener(changeListener, InvestmentNotificationType.Removed);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		historyPanel.removeListeners();
		NotificationCentre.removeListener(changeListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

}
