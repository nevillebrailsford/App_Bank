package applications.bank.gui.modified;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import application.base.app.gui.BottomColoredPanel;
import application.definition.ApplicationConfiguration;
import applications.bank.gui.BankApplicationMenu;
import applications.bank.gui.IApplication;
import applications.bank.gui.actions.BankActionFactory;
import applications.bank.gui.models.HistoryTableModel;
import applications.bank.model.Investment;

public class HistoryPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = HistoryPanel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	JTable historyTable;
	private HistoryTableModel model;
	private JButton clearSelection = new JButton("Clear selection");
	private JPopupMenu popup;
	private BankActionFactory actionFactory;

	public HistoryPanel(Investment investment, BankApplicationMenu menuBar, IApplication application) {
		LOGGER.entering(CLASS_NAME, "init", investment);
		actionFactory = BankActionFactory.instance(application);
		setLayout(new BorderLayout());
		model = new HistoryTableModel(investment);
		historyTable = new JTable(model) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void processMouseEvent(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(this, e.getX(), e.getY());
				} else {
					super.processMouseEvent(e);
				}
			}
		};
		historyTable.setFillsViewportHeight(true);
		historyTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		historyTable.setRowSelectionAllowed(true);
		JScrollPane scrollPane = new JScrollPane(historyTable);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);
		JPanel buttonPanel = new BottomColoredPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(clearSelection);
		add(buttonPanel, BorderLayout.PAGE_END);
		clearSelection.addActionListener((e) -> {
			historyTable.getSelectionModel().clearSelection();
		});
		popup = new JPopupMenu();
		JMenu viewMenu = new JMenu("View");
		viewMenu.add(actionFactory.viewInvestmentHistroyAction());
		popup.add(viewMenu);
		LOGGER.exiting(CLASS_NAME, "init");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		model.removeListeners();
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

}
