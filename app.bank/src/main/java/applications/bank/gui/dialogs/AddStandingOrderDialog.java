package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import application.model.Period;
import applications.bank.model.Account;
import applications.bank.model.StandingOrder;
import applications.bank.storage.BankMonitor;

public class AddStandingOrderDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = AddStandingOrderDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private StandingOrder standingOrder = null;
	private int result = CANCEL_PRESSED;
	private JLabel lblAmount;
	private JComboBox<Account> owningAccount;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JComboBox<Period> frequency;
	private JLabel lblNewLabel_4;
	private JTextField reference;
	private JTextField amount;
	private JComboBox<Account> recipientAccount;
	private JDateChooser startDate;
	private List<Account> accounts;

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
	public AddStandingOrderDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add a Standing Order");
		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Enter the details below to add a standing order.");
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

		JLabel lblNewLabel = new JLabel("Owning Account:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel, "2, 2, right, default");

		owningAccount = new JComboBox<>();
		owningAccount.setEditable(false);
		owningAccount.addItemListener((event) -> {
			if (owningAccount.getSelectedIndex() == -1) {
				clearRecipientAccounts();
			} else {
				loadRecipientAccounts();
			}
			okButton.setEnabled(validFields());
		});
		contentPanel.add(owningAccount, "4, 2, fill, default");

		lblAmount = new JLabel("Amount:");
		lblAmount.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAmount, "2, 4, right, default");

		amount = new JTextField();
		contentPanel.add(amount, "4, 4, fill, default");
		amount.getDocument().addDocumentListener(documentListener);
		amount.setColumns(10);

		lblNewLabel_1 = new JLabel("Frequency:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 6, right, default");

		frequency = new JComboBox<>();
		frequency.setEditable(false);
		frequency.addItemListener((event) -> {
			okButton.setEnabled(validFields());
		});
		contentPanel.add(frequency, "4, 6, fill, default");

		lblNewLabel_2 = new JLabel("Start Date:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 8, right, default");

		startDate = new JDateChooser(new Date(), "dd/MM/yyyy");
		contentPanel.add(startDate, "4, 8, fill, default");

		lblNewLabel_3 = new JLabel("Recipient Account");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_3, "2, 10, right, default");

		recipientAccount = new JComboBox<>();
		recipientAccount.addItemListener((event) -> {
			okButton.setEnabled(validFields());
		});
		contentPanel.add(recipientAccount, "4, 10, fill, default");

		lblNewLabel_4 = new JLabel("Reference:");
		contentPanel.add(lblNewLabel_4, "2, 12, right, default");

		reference = new JTextField();
		contentPanel.add(reference, "4, 12, fill, default");
		reference.getDocument().addDocumentListener(documentListener);
		reference.setColumns(10);

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Add Standing Order");
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
					LocalDate local = LocalDate.ofInstant(startDate.getDate().toInstant(), ZoneId.systemDefault());
					standingOrder = new StandingOrder.Builder().owner((Account) owningAccount.getSelectedItem())
							.amount(amount.getText()).nextActionDue(local)
							.frequency((Period) frequency.getSelectedItem())
							.recipient((Account) recipientAccount.getSelectedItem()).reference(reference.getText())
							.build();
					result = OK_PRESSED;
					setVisible(false);
				} catch (IllegalArgumentException i) {
					JOptionPane.showMessageDialog(AddStandingOrderDialog.this, "Error has occured: " + i.getMessage(),
							"Error occured", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		okButton.setEnabled(false);

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				standingOrder = null;
				result = CANCEL_PRESSED;
				setVisible(false);
			}
		});

		loadOwningAccounts();
		loadFrequency();

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

	public StandingOrder standingOrder() {
		LOGGER.entering(CLASS_NAME, "standingOrder");
		LOGGER.exiting(CLASS_NAME, "standingOrder", standingOrder);
		return standingOrder;
	}

	private boolean validFields() {
		return validOwningAccount() && !emptyTextField(amount) && validFrequency() && !emptyTextField(reference);
	}

	private boolean emptyTextField(JTextField field) {
		return field.getText().isEmpty();
	}

	private boolean validOwningAccount() {
		return owningAccount.getSelectedIndex() >= 0;
	}

	private boolean validFrequency() {
		return frequency.getSelectedIndex() >= 0;
	}

	private void loadOwningAccounts() {
		accounts = BankMonitor.instance().accounts();
		Collections.sort(accounts, (a1, a2) -> {
			return a1.accountId().accountNumber().compareTo(a2.accountId().accountNumber());
		});
		for (int i = 0; i < accounts.size(); i++) {
			owningAccount.addItem(accounts.get(i));
		}
		owningAccount.setSelectedIndex(-1);
	}

	private void loadFrequency() {
		for (Period period : Period.values()) {
			frequency.addItem(period);
		}
		frequency.setSelectedIndex(-1);
	}

	private void clearRecipientAccounts() {
		recipientAccount.removeAllItems();
	}

	private void loadRecipientAccounts() {
		clearRecipientAccounts();
		accounts = BankMonitor.instance().accounts();
		Collections.sort(accounts, (a1, a2) -> {
			return a1.accountId().accountNumber().compareTo(a2.accountId().accountNumber());
		});
		for (int i = 0; i < accounts.size(); i++) {
			Account recAccount = accounts.get(i);
			if (!recAccount.equals(owningAccount.getSelectedItem())) {
				recipientAccount.addItem(recAccount);
			}
		}
		recipientAccount.setSelectedIndex(-1);
	}
}
