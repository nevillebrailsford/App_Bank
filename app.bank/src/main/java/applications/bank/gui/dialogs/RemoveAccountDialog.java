package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Collections;
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

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Account;
import applications.bank.storage.BankMonitor;

public class RemoveAccountDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = RemoveAccountDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private Account account = null;
	private int result = CANCEL_PRESSED;
	private JLabel lblAccountHolder;
	private JComboBox<Account> accountNumber;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JTextField branch;
	private JLabel lblNewLabel_3;
	private JTextField bank;

	private JTextField accountHolder;
	private JTextField accountType;

	private List<Account> accounts;

	/**
	 * Create the dialog.
	 */
	public RemoveAccountDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Remove an Exisitng Account");

		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Select the account number to be removed.");
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

		JLabel lblNewLabel = new JLabel("Account numbered:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel, "2, 2, right, default");

		accountNumber = new JComboBox<>();
		accountNumber.setEditable(false);
		accountNumber.addItemListener((ItemEvent e) -> {
			okButton.setEnabled(false);
			if (accountNumber.getSelectedIndex() != -1) {
				loadDetails();
				okButton.setEnabled(true);
			}
		});
		contentPanel.add(accountNumber, "4, 2, fill, default");

		lblAccountHolder = new JLabel("Account Holder:");
		lblAccountHolder.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAccountHolder, "2, 4, right, default");

		accountHolder = new JTextField();
		accountHolder.setEditable(false);
		contentPanel.add(accountHolder, "4, 4, fill, default");
		accountHolder.setColumns(10);

		lblNewLabel_1 = new JLabel("Account Type:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 6, right, default");

		accountType = new JTextField();
		accountType.setEditable(false);
		contentPanel.add(accountType, "4, 6, fill, default");
		accountType.setColumns(10);

		lblNewLabel_2 = new JLabel("Branch:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 8, right, default");

		branch = new JTextField();
		branch.setEditable(false);
		contentPanel.add(branch, "4, 8, fill, default");
		branch.setColumns(10);

		lblNewLabel_3 = new JLabel("Bank:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_3, "2, 10, right, default");

		bank = new JTextField();
		bank.setEditable(false);
		contentPanel.add(bank, "4, 10, fill, default");
		bank.setColumns(10);
		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Remove Account");
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
					account = (Account) accountNumber.getSelectedItem();
					result = OK_PRESSED;
					setVisible(false);
				} catch (IllegalArgumentException i) {
					JOptionPane.showMessageDialog(RemoveAccountDialog.this, "Error has occured: " + i.getMessage(),
							"Error occured", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		okButton.setEnabled(false);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				account = null;
				result = CANCEL_PRESSED;
				setVisible(false);
			}
		});
		loadAccountDetails();
		pack();
		setLocationRelativeTo(parent);
		LOGGER.exiting(CLASS_NAME, "init");
	}

	private void loadAccountDetails() {
		accounts = BankMonitor.instance().accounts();
		Collections.sort(accounts, (a1, a2) -> {
			return a1.accountId().accountNumber().compareTo(a2.accountId().accountNumber());
		});
		for (int i = 0; i < accounts.size(); i++) {
			accountNumber.addItem(accounts.get(i));
		}
	}

	private void loadDetails() {
		if (accountNumber.getSelectedIndex() != -1) {
			Account ac = (Account) accountNumber.getSelectedItem();
			accountHolder.setText(ac.accountId().accountHolder());
			accountType.setText(ac.accountType().toString());
			branch.setText(ac.owner().toString());
			bank.setText(ac.owner().owner().name());
		} else {
			accountHolder.setText("");
			accountType.setText("");
			branch.setText("");
			bank.setText("");
		}
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

}
