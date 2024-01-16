package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;
import applications.bank.model.Bank;
import applications.bank.storage.BankMonitor;

public class RemoveBankDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = RemoveBankDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private Bank bank = null;
	private int result = CANCEL_PRESSED;
	private JComboBox<Bank> banks;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			RemoveBankDialog dialog = new RemoveBankDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public RemoveBankDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Remove An Existing Bank");

		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Select the bank to delete.");
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
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lblNewLabel = new JLabel("Bank Name:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel, "2, 2, right, default");

		banks = new JComboBox<>();
		banks.setEditable(false);
		contentPanel.add(banks, "4, 2, fill, default");
		banks.addItemListener((itemEvent) -> {
			okButton.setEnabled(false);
			if (banks.getSelectedIndex() != -1) {
				okButton.setEnabled(true);
			}
		});

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Remove Selected Bank");
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
				bank = (Bank) banks.getSelectedItem();
				result = OK_PRESSED;
				setVisible(false);
			}
		});
		okButton.setEnabled(false);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bank = null;
				result = CANCEL_PRESSED;
				setVisible(false);
			}
		});

		loadBanks();

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

	public Bank bank() {
		LOGGER.entering(CLASS_NAME, "bank");
		LOGGER.exiting(CLASS_NAME, "bank", bank);
		return bank;
	}

	private void loadBanks() {
		for (Bank bank : BankMonitor.instance().banks()) {
			banks.addItem(bank);
		}
		banks.setSelectedIndex(-1);
	}
}
