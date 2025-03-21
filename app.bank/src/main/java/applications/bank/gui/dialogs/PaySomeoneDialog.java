package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import applications.bank.model.Account;
import applications.bank.model.Transaction;
import applications.bank.storage.BankMonitor;

public class PaySomeoneDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = PaySomeoneDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private Transaction transaction = null;
	private int result = CANCEL_PRESSED;
	private JLabel lblAccountHolder;
	private JComboBox<Account> accountNumber;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JDateChooser dateOfPayment;

	private JTextField amount;
	private JComboBox<String> description;

	private List<Account> accounts;

	private Account account = null;

	DocumentListener documentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			okButton.setEnabled(validFields());
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			okButton.setEnabled(validFields());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			okButton.setEnabled(validFields());
		}
	};

	FocusListener focusListener = new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			okButton.setEnabled(validFields());
		}

		@Override
		public void focusLost(FocusEvent e) {
		}

	};

	/**
	 * Create the dialog.
	 */
	public PaySomeoneDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Pay Someone");

		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Enter the details below to pay someone.");
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
						FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lblNewLabel = new JLabel("From Account:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel, "2, 2, right, default");

		accountNumber = new JComboBox<>();
		accountNumber.setEditable(false);
		accountNumber.addItemListener((ItemEvent e) -> {
			okButton.setEnabled(validFields());
		});
		contentPanel.add(accountNumber, "4, 2, fill, default");

		lblAccountHolder = new JLabel("Amount:");
		lblAccountHolder.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAccountHolder, "2, 4, right, default");

		amount = new JTextField();
		amount.getDocument().addDocumentListener(documentListener);
		contentPanel.add(amount, "4, 4, fill, default");
		amount.setColumns(10);

		lblNewLabel_1 = new JLabel("Description:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 6, right, default");

		description = new JComboBox<>();
		description.setEditable(true);
		description.addActionListener((event) -> {
			okButton.setEnabled(validFields());
		});
		contentPanel.add(description, "4, 6, fill, default");

		lblNewLabel_2 = new JLabel("Date of Payment:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 8, right, default");

		dateOfPayment = new JDateChooser(new Date(), "dd/MM/yyyy");
		contentPanel.add(dateOfPayment, "4, 8, fill, default");

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Pay someone");
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
		{
			cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}
		okButton.addActionListener((event) -> {
			try {
				Money val = new Money(amount.getText());
				val = val.negate();
				LocalDate local = LocalDate.ofInstant(dateOfPayment.getDate().toInstant(), ZoneId.systemDefault());
				transaction = new Transaction.Builder().account((Account) accountNumber.getSelectedItem()).amount(val)
						.description((String) description.getSelectedItem()).date(local).build();
				result = OK_PRESSED;
				DescriptionComboHelper.saveDescriptionOptions(description);
				setVisible(false);
			} catch (IllegalArgumentException i) {
				JOptionPane.showMessageDialog(PaySomeoneDialog.this, "Error has occured: " + i.getMessage(),
						"Error occured", JOptionPane.ERROR_MESSAGE);
			}
		});
		okButton.setEnabled(false);
		cancelButton.addActionListener((event) -> {
			transaction = null;
			result = CANCEL_PRESSED;
			DescriptionComboHelper.saveDescriptionOptions(description);
			setVisible(false);
		});
		description.addFocusListener(focusListener);
		loadAccountDetails();
		DescriptionComboHelper.loadDescriptionOptions(description);
		pack();
		setLocationRelativeTo(parent);
		LOGGER.exiting(CLASS_NAME, "init");
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	private void loadAccountDetails() {
		accounts = BankMonitor.instance().accounts();
		Collections.sort(accounts, (a1, a2) -> {
			return a1.accountId().accountNumber().compareTo(a2.accountId().accountNumber());
		});
		for (int i = 0; i < accounts.size(); i++) {
			accountNumber.addItem(accounts.get(i));
		}
		accountNumber.setSelectedIndex(-1);
	}

	private boolean validFields() {
		return accountSelected() && !emptyTextField(amount) && !emptyComboBox(description);
	}

	private boolean accountSelected() {
		return accountNumber.getSelectedIndex() >= 0;
	}

	private boolean emptyTextField(JTextField field) {
		return field.getText().isEmpty();
	}

	private boolean emptyComboBox(JComboBox<String> box) {
		return box.getSelectedItem() == null;
	}

	public int displayAndWait() {
		LOGGER.exiting(CLASS_NAME, "displayAndWait");
		if (account != null) {
			accountNumber.setEditable(true);
			accountNumber.setSelectedItem(account);
			accountNumber.setEnabled(false);
		}
		setVisible(true);
		LOGGER.exiting(CLASS_NAME, "displayAndWait", result);
		return result;
	}

	public Transaction transaction() {
		LOGGER.entering(CLASS_NAME, "transaction");
		LOGGER.exiting(CLASS_NAME, "transaction", transaction);
		return transaction;
	}

}
