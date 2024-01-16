package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
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

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Investment;
import applications.bank.storage.BankMonitor;

public class RemoveInvestmentDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = RemoveInvestmentDialog.class.getName();
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
	private Investment investment = null;

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
	private JTextField date;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			RemoveInvestmentDialog dialog = new RemoveInvestmentDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public RemoveInvestmentDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add an Investment");
		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Choose the investment to be removed.");
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
		investments.addItemListener((ItemEvent e) -> {
			okButton.setEnabled(false);
			if (investments.getSelectedIndex() != -1) {
				loadDetails();
				okButton.setEnabled(true);
			}
		});
		contentPanel.add(investments, "4, 2, fill, default");

		lblNewLabel_1 = new JLabel("Value");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 4, right, default");

		value = new JTextField();
		value.setEditable(false);
		value.getDocument().addDocumentListener(documentListener);
		contentPanel.add(value, "4, 4, fill, top");
		value.setColumns(10);

		lblNewLabel_2 = new JLabel("Date:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 6, right, default");

		date = new JTextField();
		date.setEditable(false);
		contentPanel.add(date, "4, 6, fill, default");
		date.setColumns(10);

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Remove Investment");
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
					investment = (Investment) investments.getSelectedItem();
					result = OK_PRESSED;
					setVisible(false);
				} catch (IllegalArgumentException i) {
					JOptionPane.showMessageDialog(RemoveInvestmentDialog.this, "Error has occured: " + i.getMessage(),
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

	private boolean validFields() {
		return !emptyTextField(value);
	}

	private boolean emptyTextField(JTextField field) {
		return field.getText().isEmpty();
	}

	private void loadDetails() {
		if (investments.getSelectedIndex() != -1) {
			Investment inv = (Investment) investments.getSelectedItem();
			value.setText(inv.value().cost());
			date.setText(LocalDate.now().toString());
		} else {
			value.setText("");
			date.setText("");
		}
	}

	private void loadInvestments() {
		for (Investment investment : BankMonitor.instance().investments()) {
			investments.addItem(investment);
		}
		investments.setSelectedIndex(-1);
	}
}
