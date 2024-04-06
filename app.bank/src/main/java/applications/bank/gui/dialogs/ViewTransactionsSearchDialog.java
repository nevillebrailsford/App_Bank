package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import application.base.app.gui.ColoredPanel;
import applications.bank.gui.models.SearchTransactionsTableModel;

public class ViewTransactionsSearchDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new ColoredPanel();
	private JTable transactionsTable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ViewTransactionsSearchDialog dialog = new ViewTransactionsSearchDialog(null, null, null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ViewTransactionsSearchDialog(JFrame parent, SearchTransactionsTableModel model, String search) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Transactions for search " + search);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout());
		transactionsTable = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(transactionsTable);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);
		setSize(new Dimension(700, 400));
		setLocationRelativeTo(parent);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			}
		});
	}

}
