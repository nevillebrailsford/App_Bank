package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.toedter.calendar.JDateChooser;

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;

public class SearchTransactionsDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = SearchTransactionsDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private int result = CANCEL_PRESSED;
	private JTextField searchString;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JDateChooser fromDate;
	private JDateChooser toDate;

	/**
	 * Create the dialog.
	 */
	public SearchTransactionsDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Search Transactions");
		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Enter the details below to search within transactions.");
			instructions.setFont(new Font("Tahoma", Font.PLAIN, 18));
			instructions.setHorizontalAlignment(SwingConstants.CENTER);
			instructions.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(instructions, BorderLayout.NORTH);
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lblNewLabel = new JLabel("Search For:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel, "2, 2, right, default");

		searchString = new JTextField();
		contentPanel.add(searchString, "4, 2, fill, default");
		searchString.setColumns(10);

		lblNewLabel_1 = new JLabel("From Date:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 4, right, default");

		fromDate = new JDateChooser();
		contentPanel.add(fromDate, "4, 4, fill, default");

		lblNewLabel_2 = new JLabel("To Date:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 6, right, default");

		toDate = new JDateChooser();
		contentPanel.add(toDate, "4, 6, fill, default");

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Perform Search");
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
		{
			cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = OK_PRESSED;
				setVisible(false);
			}
		});
		okButton.setEnabled(false);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = CANCEL_PRESSED;
				setVisible(false);
			}
		});
		searchString.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				checkSearchString();
			}

			@Override
			public void focusGained(FocusEvent e) {
				searchString.selectAll();
			}
		});
		searchString.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				checkSearchString();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkSearchString();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkSearchString();
			}
		});

		pack();
		setLocationRelativeTo(parent);
		LOGGER.exiting(CLASS_NAME, "init");
	}

	public int displayAndWait() {
		LOGGER.exiting(CLASS_NAME, "displayAndWait");
		setVisible(true);
		LOGGER.exiting(CLASS_NAME, "displayAndWait", result);
		return result;
	}

	public String searchString() {
		return searchString.getText();
	}

	public LocalDate fromDate() {
		if (fromDate.getDate() == null) {
			return LocalDate.of(2000, 1, 1);
		}
		LocalDate date = fromDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return date;
	}

	public LocalDate toDate() {
		if (toDate.getDate() == null) {
			return LocalDate.now().plusDays(1);
		}
		LocalDate date = toDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return date;
	}

	private void checkSearchString() {
		if (validFields()) {
			okButton.setEnabled(true);
		} else {
			okButton.setEnabled(false);
		}
	}

	private boolean validFields() {
		return !emptyTextField(searchString);
	}

	private boolean emptyTextField(JTextField field) {
		return field.getText().isEmpty();
	}

}
