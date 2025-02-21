package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;
import applications.bank.gui.modified.DateRangePanel;

public class CategorySpendingDateSelectionDialog extends JDialog {
	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = SearchTransactionsDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JButton okButton;
	private JButton cancelButton;
	private int result = CANCEL_PRESSED;
	private DateRangePanel dateRangePanel;
	
	public CategorySpendingDateSelectionDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLayout(new BorderLayout());
		contentPanel = new ColoredPanel();
		dateRangePanel = new DateRangePanel();
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(dateRangePanel);
		
		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Print Report");
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
		okButton.addActionListener(e -> {
			result = OK_PRESSED;
			setVisible(false);
		});
		{
			cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}
		cancelButton.addActionListener(e -> {
			result = CANCEL_PRESSED;
			setVisible(false);
		});
		pack();
		setLocationRelativeTo(parent);
	}

	public int displayAndWait() {
		LOGGER.exiting(CLASS_NAME, "displayAndWait");
		setVisible(true);
		LOGGER.exiting(CLASS_NAME, "displayAndWait", result);
		return result;
	}

	public LocalDate fromDate() {
		return dateRangePanel.fromDate();
	}

	public LocalDate toDate() {
		return dateRangePanel.toDate();
	}

	public static void main(String[] args) {
		try {
			CategorySpendingDateSelectionDialog dialog = new CategorySpendingDateSelectionDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			System.out.println(dialog.displayAndWait());
			System.out.println(dialog.fromDate());
			System.out.println(dialog.toDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
