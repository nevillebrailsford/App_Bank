package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;
import applications.bank.model.StandingOrder;
import applications.bank.storage.BankMonitor;

public class RemoveStandingOrderDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = RemoveStandingOrderDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private StandingOrder standingOrder = null;
	private int result = CANCEL_PRESSED;
	private JLabel lblAmount;
	private JComboBox<StandingOrder> reference;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JTextField owningAccount;
	private JTextField amount;
	private List<StandingOrder> standingOrders;

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
	private JTextField frequency;
	private JTextField startDate;
	private JTextField recipientAccount;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			RemoveStandingOrderDialog dialog = new RemoveStandingOrderDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public RemoveStandingOrderDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add a Standing Order");
		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Select the standing order to be removed.");
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

		JLabel lblNewLabel = new JLabel("Reference:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel, "2, 2, right, default");

		reference = new JComboBox<>();
		reference.setEditable(false);
		reference.addItemListener((event) -> {
			loadInformation();
			okButton.setEnabled(validFields());
		});
		contentPanel.add(reference, "4, 2, fill, default");

		lblAmount = new JLabel("Amount:");
		lblAmount.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAmount, "2, 4, right, default");

		amount = new JTextField();
		amount.setEditable(false);
		contentPanel.add(amount, "4, 4, fill, default");
		amount.setColumns(10);

		lblNewLabel_1 = new JLabel("Frequency:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 6, right, default");

		frequency = new JTextField();
		frequency.setEditable(false);
		contentPanel.add(frequency, "4, 6, fill, default");
		frequency.setColumns(10);

		lblNewLabel_2 = new JLabel("Start Date:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 8, right, default");

		startDate = new JTextField();
		startDate.setEditable(false);
		contentPanel.add(startDate, "4, 8, fill, default");
		startDate.setColumns(10);

		lblNewLabel_3 = new JLabel("Recipient Account");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_3, "2, 10, right, default");

		recipientAccount = new JTextField();
		recipientAccount.setEditable(false);
		contentPanel.add(recipientAccount, "4, 10, fill, default");
		recipientAccount.setColumns(10);

		lblNewLabel_4 = new JLabel("Owning Account:");
		contentPanel.add(lblNewLabel_4, "2, 12, right, default");

		owningAccount = new JTextField();
		owningAccount.setEditable(false);
		contentPanel.add(owningAccount, "4, 12, fill, default");
		owningAccount.getDocument().addDocumentListener(documentListener);
		owningAccount.setColumns(10);

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Remove Standing Order");
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
					standingOrder = (StandingOrder) reference.getSelectedItem();
					result = OK_PRESSED;
					setVisible(false);
				} catch (IllegalArgumentException i) {
					JOptionPane.showMessageDialog(RemoveStandingOrderDialog.this,
							"Error has occured: " + i.getMessage(), "Error occured", JOptionPane.ERROR_MESSAGE);
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

		loadStandingOrders();

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
		return validReference();
	}

	private void loadStandingOrders() {
		standingOrders = BankMonitor.instance().standingOrders();
		Collections.sort(standingOrders, (so1, so2) -> {
			return so1.reference().compareTo(so2.reference());
		});
		for (int i = 0; i < standingOrders.size(); i++) {
			reference.addItem(standingOrders.get(i));
		}
		reference.setSelectedIndex(-1);
	}

	private boolean validReference() {
		return reference.getSelectedIndex() >= 0;
	}

	private void loadInformation() {
		if (reference.getSelectedIndex() >= 0) {
			StandingOrder standingOrder = (StandingOrder) reference.getSelectedItem();
			amount.setText(standingOrder.amount().cost());
			frequency.setText(standingOrder.frequency().toString());
			startDate.setText(standingOrder.nextActionDue().toString());
			recipientAccount.setText(standingOrder.recipient().accountId().accountNumber());
			owningAccount.setText(standingOrder.owner().accountId().accountNumber());
		} else {
			amount.setText("");
			frequency.setText("");
			startDate.setText("");
			recipientAccount.setText("");
			owningAccount.setText("");
		}
	}
}
