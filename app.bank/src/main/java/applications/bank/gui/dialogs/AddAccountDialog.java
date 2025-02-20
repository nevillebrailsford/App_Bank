package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
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
import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.thread.ThreadServices;
import applications.bank.gui.changes.AddBranchChange;
import applications.bank.model.Account;
import applications.bank.model.AccountType;
import applications.bank.model.Bank;
import applications.bank.model.Branch;
import applications.bank.storage.BankMonitor;

public class AddAccountDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = AddAccountDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private Account account = null;
	private int result = CANCEL_PRESSED;
	private JLabel label;
	private JButton newBranchButton;
	private JComboBox<Branch> branchId;
	private JComboBox<Bank> bankName;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JTextField accountHolder;
	private JLabel lblNewLabel_3;
	private JTextField accountNumber;
	private JComboBox<AccountType> accountType;

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
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddAccountDialog dialog = new AddAccountDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddAccountDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add an Account");
		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Enter the details below to add an account.");
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

		JLabel lblNewLabel = new JLabel("Bank Name:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel, "2, 2, right, default");

		bankName = new JComboBox<>();
		bankName.setEditable(false);
		bankName.addItemListener((ItemEvent e) -> {
			if (bankName.getSelectedIndex() != -1) {
				clearBranchDetails();
				loadBranchDetails();
				okButton.setEnabled(false);
				newBranchButton.setEnabled(true);
			} else {
				newBranchButton.setEnabled(false);
			}
			if (validFields()) {
				okButton.setEnabled(true);
			}
		});
		contentPanel.add(bankName, "4, 2, fill, default");

		label = new JLabel("Branch:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(label, "2, 4, right, default");

		branchId = new JComboBox<>();
		branchId.setEditable(false);
		branchId.addItemListener((ItemEvent e) -> {
			okButton.setEnabled(validFields());
		});
		contentPanel.add(branchId, "4, 4, fill, default");

		newBranchButton = new JButton("New Branch");
		contentPanel.add(newBranchButton, "6, 4");

		lblNewLabel_1 = new JLabel("Account Type:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 6, right, default");

		accountType = new JComboBox<>();
		accountType.setEditable(false);
		contentPanel.add(accountType, "4, 6, fill, default");

		lblNewLabel_2 = new JLabel("Account Holder:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 8, right, default");

		accountHolder = new JTextField();
		accountHolder.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				accountHolder.selectAll();
				okButton.setEnabled(false);
			}

			@Override
			public void focusLost(FocusEvent e) {
				okButton.setEnabled(validFields());
			}
		});
		accountHolder.getDocument().addDocumentListener(documentListener);
		contentPanel.add(accountHolder, "4, 8, fill, default");
		accountHolder.setColumns(20);

		lblNewLabel_3 = new JLabel("Account Number:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_3, "2, 10, right, default");

		accountNumber = new JTextField();
		accountNumber.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				accountNumber.selectAll();
				okButton.setEnabled(false);
			}

			@Override
			public void focusLost(FocusEvent e) {
				okButton.setEnabled(validFields());
			}
		});
		accountNumber.getDocument().addDocumentListener(documentListener);
		contentPanel.add(accountNumber, "4, 10, fill, default");
		accountNumber.setColumns(20);

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Add Account");
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
					account = new Account.Builder().accountHolder(accountHolder.getText())
							.accountNumber(accountNumber.getText())
							.accountType((AccountType) accountType.getSelectedItem())
							.branch((Branch) branchId.getSelectedItem()).build();
					result = OK_PRESSED;
					setVisible(false);
				} catch (IllegalArgumentException i) {
					JOptionPane.showMessageDialog(AddAccountDialog.this, "Error has occured: " + i.getMessage(),
							"Error occured", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		okButton.setEnabled(false);
		newBranchButton.addActionListener((e) -> {
			AddBranchDialog dialog = new AddBranchDialog(null, (Bank) bankName.getSelectedItem());
			int result = dialog.displayAndWait();
			if (result == AddBranchDialog.OK_PRESSED) {
				Branch newBranch = dialog.branch();
				AddBranchChange newBranchChange = new AddBranchChange(newBranch);
				ThreadServices.instance().executor().submit(() -> {
					ChangeManager.instance().execute(newBranchChange);
				});
				JOptionPane.showMessageDialog(this, "Branch " + newBranch + " has been created", "Branch Added",
						JOptionPane.INFORMATION_MESSAGE);
				insertBranch(newBranch);
			}
			dialog.dispose();
		});
		newBranchButton.setEnabled(false);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				account = null;
				result = CANCEL_PRESSED;
				setVisible(false);
			}
		});
		loadBankDetails();
		loadAccountTypeDetails();
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

	public Account account() {
		LOGGER.entering(CLASS_NAME, "account");
		LOGGER.exiting(CLASS_NAME, "account", account);
		return account;
	}

	private boolean validFields() {
		return bankSelected() && branchSelected() && typeSelected() && !emptyTextField(accountHolder)
				&& !emptyTextField(accountNumber);
	}

	private boolean bankSelected() {
		return bankName.getSelectedIndex() != -1;
	}

	private boolean branchSelected() {
		return branchId.getSelectedIndex() != -1;
	}

	private boolean typeSelected() {
		return accountType.getSelectedIndex() != -1;
	}

	private boolean emptyTextField(JTextField field) {
		return field.getText().isEmpty();
	}

	private void loadBankDetails() {
		for (Bank bank : BankMonitor.instance().banks()) {
			bankName.addItem(bank);
		}
		bankName.setSelectedIndex(-1);
	}

	private void loadAccountTypeDetails() {
		for (AccountType aType : AccountType.values()) {
			accountType.addItem(aType);
		}
		accountType.setSelectedIndex(0);
	}

	private void loadBranchDetails() {
		Bank selectedBank = (Bank) bankName.getSelectedItem();
		selectedBank.branches().forEach(branch -> {
			branchId.addItem(branch);
		});
		branchId.setSelectedIndex(-1);
	}

	private void clearBranchDetails() {
		branchId.removeAllItems();
	}

	private void insertBranch(Branch newBranch) {
		int index = locatePosition(newBranch);
		if (index >= 0) {
			branchId.insertItemAt(newBranch, index);
		} else {
			branchId.addItem(newBranch);
		}
	}

	private int locatePosition(Branch newBranch) {
		int index = -1;
		for (int i = 0; i < branchId.getItemCount(); i++) {
			if (branchId.getItemAt(i).compareTo(newBranch) > 0) {
				index = i;
				break;
			}
		}
		return index;
	}
}
