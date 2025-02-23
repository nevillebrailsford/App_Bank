package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import applications.bank.model.Transfer;
import applications.bank.storage.BankMonitor;

public class TransferDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = TransferDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private Transfer transfer = null;
	private int result = CANCEL_PRESSED;
	private JLabel lblAccountHolder;
	private JComboBox<Account> fromAccount;
	private JComboBox<Account> toAccount;
	private JLabel lblAmount;
	private JTextField amount;
	private JLabel lblDate;
	private JDateChooser dateOfTransfer;
	private JLabel lblDescription;
	private JComboBox<String> description;

	private List<Account> accounts;

	private Account account;

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

	/**
	 * Create the dialog.
	 */
	public TransferDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Transfer");

		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Complete the form to transfer money between accounts.");
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

		fromAccount = new JComboBox<>();
		fromAccount.setEditable(false);
		fromAccount.addItemListener((ItemEvent e) -> {
			loadToAccounts();
			okButton.setEnabled(validFields());
		});
		contentPanel.add(fromAccount, "4, 2, fill, default");

		lblAccountHolder = new JLabel("To Account:");
		lblAccountHolder.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAccountHolder, "2, 4, right, default");

		toAccount = new JComboBox<>();
		toAccount.setEditable(false);
		contentPanel.add(toAccount, "4, 4, fill, default");
		toAccount.addItemListener((ItemEvent e) -> {
			okButton.setEnabled(validFields());
		});

		lblAmount = new JLabel("Amount:");
		lblAmount.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAmount, "2, 6, right, default");

		amount = new JTextField();
		amount.getDocument().addDocumentListener(documentListener);
		contentPanel.add(amount, "4, 6, fill, default");
		amount.setColumns(10);

		lblDate = new JLabel("Date:");
		lblDate.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblDate, "2, 8, right, default");

		dateOfTransfer = new JDateChooser(new Date(), "dd/MM/yyyy");
		contentPanel.add(dateOfTransfer, "4, 8, fill, default");

		lblDescription = new JLabel("Description:");
		lblDescription.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblDescription, "2, 10, right, default");

		description = new JComboBox<>();
		description.setEditable(true);
		contentPanel.add(description, "4, 10, fill, default");

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Transfer");
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
				Account accountFrom = (Account) fromAccount.getSelectedItem();
				Account accountTo = (Account) toAccount.getSelectedItem();
				LocalDate local = LocalDate.ofInstant(dateOfTransfer.getDate().toInstant(), ZoneId.systemDefault());
				transfer = new Transfer(accountFrom, accountTo, val, local, (String) description.getSelectedItem());
				result = OK_PRESSED;
				DescriptionComboHelper.saveDescriptionOptions(description);
				setVisible(false);
			} catch (IllegalArgumentException i) {
				JOptionPane.showMessageDialog(TransferDialog.this, "Error has occured: " + i.getMessage(),
						"Error occured", JOptionPane.ERROR_MESSAGE);
			}
		});
		okButton.setEnabled(false);
		cancelButton.addActionListener((event) -> {
			transfer = null;
			result = CANCEL_PRESSED;
			DescriptionComboHelper.saveDescriptionOptions(description);
			setVisible(false);
		});
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
		accounts = BankMonitor.instance().accounts().stream().filter((account) -> account.active())
				.collect(Collectors.toList());
		Collections.sort(accounts, (a1, a2) -> {
			return a1.accountId().accountNumber().compareTo(a2.accountId().accountNumber());
		});
		for (int i = 0; i < accounts.size(); i++) {
			fromAccount.addItem(accounts.get(i));
		}
		fromAccount.setSelectedIndex(-1);
	}

	private void loadToAccounts() {
		if (fromAccount.getSelectedIndex() == -1) {
			clearToAccounts();
		} else {
			accounts = BankMonitor.instance().accounts().stream().filter((account) -> account.active())
					.collect(Collectors.toList());
			Collections.sort(accounts, (a1, a2) -> {
				return a1.accountId().accountNumber().compareTo(a2.accountId().accountNumber());
			});
			Account selectedAccount = (Account) fromAccount.getSelectedItem();
			for (int i = 0; i < accounts.size(); i++) {
				if (!selectedAccount.equals(accounts.get(i))) {
					toAccount.addItem(accounts.get(i));
				}
			}
		}
		toAccount.setSelectedIndex(-1);
	}

	private void clearToAccounts() {
		toAccount.removeAllItems();
	}

	private boolean validFields() {
		return accountSelected() && toAccountSelected() && !emptyTextField(amount);
	}

	private boolean accountSelected() {
		return fromAccount.getSelectedIndex() >= 0;
	}

	private boolean toAccountSelected() {
		return toAccount.getSelectedIndex() >= 0;
	}

	private boolean emptyTextField(JTextField field) {
		return field.getText().isEmpty();
	}

	public int displayAndWait() {
		LOGGER.exiting(CLASS_NAME, "displayAndWait");
		if (account != null) {
			fromAccount.setEditable(true);
			fromAccount.setSelectedItem(account);
			fromAccount.setEnabled(false);
		}
		setVisible(true);
		LOGGER.exiting(CLASS_NAME, "displayAndWait", result);
		return result;
	}

	public Transfer transfer() {
		LOGGER.entering(CLASS_NAME, "transfer");
		LOGGER.exiting(CLASS_NAME, "transfer", transfer);
		return transfer;
	}

}
