package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import application.base.app.gui.ColoredPanel;
import application.base.app.gui.DateCellRenderer;
import application.base.app.gui.MoneyCellRenderer;
import application.base.app.gui.StringCellRenderer;
import application.model.Money;
import applications.bank.gui.models.StandingOrdersTableModel;
import applications.bank.model.Account;

public class ViewStandingOrdersDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new ColoredPanel();
	private JTable standingOrderTable;
	private StandingOrdersTableModel model;
	private StringCellRenderer stringCellRenderer;
	private DateCellRenderer dateCellRenderer;
	private MoneyCellRenderer moneyCellRenderer;

	/**
	 * Create the dialog.
	 */
	public ViewStandingOrdersDialog(JFrame parent, Account account) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Standing Orders for " + account.toFullString());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout());
		stringCellRenderer = new StringCellRenderer();
		dateCellRenderer = new DateCellRenderer();
		moneyCellRenderer = new MoneyCellRenderer();
		model = new StandingOrdersTableModel(account);
		standingOrderTable = new JTable(model);
		standingOrderTable.setDefaultRenderer(String.class, stringCellRenderer);
		standingOrderTable.setDefaultRenderer(LocalDate.class, dateCellRenderer);
		standingOrderTable.setDefaultRenderer(Money.class, moneyCellRenderer);
		configureStandingOrderTable();
		standingOrderTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		standingOrderTable.setRowSelectionAllowed(true);
		JScrollPane scrollPane = new JScrollPane(standingOrderTable);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);
		setSize(new Dimension(600, 400));
		setLocationRelativeTo(parent);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				removeListeners();
			}
		});
	}

	private void removeListeners() {
		model.removeListeners();
	}

	private void configureStandingOrderTable() {
		standingOrderTable.setFillsViewportHeight(true);
		standingOrderTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		standingOrderTable.setRowSelectionAllowed(true);
		standingOrderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		standingOrderTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		standingOrderTable.getColumnModel().getColumn(0).setMinWidth(100);
		standingOrderTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		standingOrderTable.getColumnModel().getColumn(1).setMinWidth(100);
		standingOrderTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		standingOrderTable.getColumnModel().getColumn(2).setMinWidth(100);
		standingOrderTable.getColumnModel().getColumn(2).setMaxWidth(100);
		standingOrderTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		standingOrderTable.getColumnModel().getColumn(3).setMinWidth(100);
		standingOrderTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		standingOrderTable.getColumnModel().getColumn(4).setMinWidth(100);
		standingOrderTable.getColumnModel().getColumn(5).setPreferredWidth(100);
		standingOrderTable.getColumnModel().getColumn(5).setMinWidth(100);
	}
}
