package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import application.model.Money;
import applications.bank.model.Investment;
import applications.bank.storage.BankMonitor;

public class ChangeInvestmentDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = ChangeInvestmentDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private int result = CANCEL_PRESSED;
	private JLabel lblSortCode;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JTextField value;
	private JDateChooser date;
	private Investment investment = null;
	private Investment oldInvestment = null;

	DocumentListener documentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			okButton.setEnabled(false);
			if (validFields()) {
				okButton.setEnabled(true);
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			okButton.setEnabled(false);
			if (validFields()) {
				okButton.setEnabled(true);
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			okButton.setEnabled(false);
			if (validFields()) {
				okButton.setEnabled(true);
			}
		}
	};
	private JComboBox<Investment> investments;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ChangeInvestmentDialog dialog = new ChangeInvestmentDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ChangeInvestmentDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add an Investment");
		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Enter the details below to update an investment.");
			instructions.setFont(new Font("Tahoma", Font.PLAIN, 18));
			instructions.setHorizontalAlignment(SwingConstants.CENTER);
			instructions.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(instructions, BorderLayout.NORTH);
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC, },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		lblSortCode = new JLabel("Name:");
		lblSortCode.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblSortCode, "2, 2, right, default");

		investments = new JComboBox<>();
		contentPanel.add(investments, "4, 2, fill, default");

		lblNewLabel_1 = new JLabel("Value");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 4, right, default");

		value = new JTextField();
		value.getDocument().addDocumentListener(documentListener);
		contentPanel.add(value, "4, 4, fill, top");
		value.setColumns(10);

		lblNewLabel_2 = new JLabel("Date:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 6, right, default");

		date = new JDateChooser(new Date(), "dd/MM/yyyy");
		contentPanel.add(date, "4, 6, fill, default");

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Update Investment");
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
				try {
					oldInvestment = (Investment) investments.getSelectedItem();
					investment = new Investment(oldInvestment);
					Money money = new Money(value.getText());
					LocalDate local = LocalDate.ofInstant(date.getDate().toInstant(), ZoneId.systemDefault());
					investment.update(money, local);
					result = OK_PRESSED;
					setVisible(false);
				} catch (IllegalArgumentException i) {
					JOptionPane.showMessageDialog(ChangeInvestmentDialog.this, "Error has occured: " + i.getMessage(),
							"Error occured", JOptionPane.ERROR_MESSAGE);
					value.setText("");
				}
			}
		});
		okButton.setEnabled(false);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				investment = null;
				result = CANCEL_PRESSED;
				setVisible(false);
			}
		});

		loadInvestments();

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

	public Investment investment() {
		LOGGER.entering(CLASS_NAME, "investment");
		LOGGER.exiting(CLASS_NAME, "investment", investment);
		return investment;
	}

	public Investment oldInvestment() {
		LOGGER.entering(CLASS_NAME, "oldInvestment");
		LOGGER.exiting(CLASS_NAME, "oldInvestment", oldInvestment);
		return oldInvestment;
	}

	private boolean validFields() {
		return !emptyTextField(value);
	}

	private boolean emptyTextField(JTextField field) {
		return field.getText().isEmpty();
	}

	private void loadInvestments() {
		for (Investment investment : BankMonitor.instance().investments()) {
			investments.addItem(investment);
		}
		investments.setSelectedIndex(-1);
	}
}
