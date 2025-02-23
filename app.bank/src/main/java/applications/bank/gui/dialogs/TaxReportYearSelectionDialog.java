package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;
import applications.bank.gui.modified.YearRangePanel;

public class TaxReportYearSelectionDialog extends JDialog {
	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = TaxReportYearSelectionDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JButton okButton;
	private JButton cancelButton;
	private int result = CANCEL_PRESSED;
	private YearRangePanel yearRangePanel;
	
	public TaxReportYearSelectionDialog(JFrame parent, int fromDate, int toDate) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLayout(new BorderLayout());
		contentPanel = new ColoredPanel();
		yearRangePanel = new YearRangePanel(fromDate, toDate);
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(yearRangePanel);
		
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
	
	public String years() {
		return yearRangePanel.years();
	}

}
