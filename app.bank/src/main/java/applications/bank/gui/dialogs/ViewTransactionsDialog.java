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
import applications.bank.gui.models.TransactionsTableModel;
import applications.bank.model.Account;

public class ViewTransactionsDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new ColoredPanel();
	private JTable transactionsTable;
	private TransactionsTableModel model;
	private StringCellRenderer stringCellRenderer;
	private DateCellRenderer dateCellRenderer;
	private MoneyCellRenderer moneyCellRenderer;

	/**
	 * Create the dialog.
	 */
	public ViewTransactionsDialog(JFrame parent, Account account) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Transactions for " + account.toFullString());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout());
		stringCellRenderer = new StringCellRenderer();
		dateCellRenderer = new DateCellRenderer();
		moneyCellRenderer = new MoneyCellRenderer();
		model = new TransactionsTableModel(account);
		transactionsTable = new JTable(model);
		transactionsTable.setDefaultRenderer(String.class, stringCellRenderer);
		transactionsTable.setDefaultRenderer(LocalDate.class, dateCellRenderer);
		transactionsTable.setDefaultRenderer(Money.class, moneyCellRenderer);
		configureTransactionTable();
		JScrollPane scrollPane = new JScrollPane(transactionsTable);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);
		setSize(new Dimension(900, 400));
		setLocationRelativeTo(parent);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				model.removeListeners();
			}
		});
	}

	private void configureTransactionTable() {
		transactionsTable.setFillsViewportHeight(true);
		transactionsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		transactionsTable.setRowSelectionAllowed(true);
		transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		transactionsTable.getColumnModel().getColumn(0).setMinWidth(100);
		transactionsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
		transactionsTable.getColumnModel().getColumn(1).setMinWidth(200);
		transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(300);
		transactionsTable.getColumnModel().getColumn(2).setMinWidth(300);
		transactionsTable.getColumnModel().getColumn(2).setMaxWidth(800);
		transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
		transactionsTable.getColumnModel().getColumn(3).setMinWidth(100);
	}

}
