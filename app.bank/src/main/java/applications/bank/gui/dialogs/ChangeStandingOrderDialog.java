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
import application.model.Money;
import application.model.Period;
import applications.bank.model.StandingOrder;
import applications.bank.storage.BankMonitor;

public class ChangeStandingOrderDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = ChangeStandingOrderDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private StandingOrder standingOrder = null;
	private int result = CANCEL_PRESSED;
	private JLabel lblAmount;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JComboBox<Period> frequency;
	private JLabel lblNewLabel_4;
	private JTextField reference;
	private JTextField amount;
	private JTextField startDate;
	private JTextField recipientAccount;
	private JComboBox<StandingOrder> standingOrders;
	private JLabel lblNewLabel_5;
	private JTextField owningAccount;
	private List<StandingOrder> orders;
	private StandingOrder changedStandingOrder;

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
			ChangeStandingOrderDialog dialog = new ChangeStandingOrderDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ChangeStandingOrderDialog(JFrame parent) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Update a Standing Order");
		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Enter the details below to update a standing order.");
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
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		lblNewLabel_5 = new JLabel("Standing Order:");
		contentPanel.add(lblNewLabel_5, "2, 2, right, default");

		standingOrders = new JComboBox<>();
		contentPanel.add(standingOrders, "4, 2, fill, default");
		standingOrders.addItemListener((event) -> {
			if (standingOrders.getSelectedIndex() == -1) {
				clearFields();
			} else {
				populateFields();
			}
		});

		JLabel lblNewLabel = new JLabel("Owning Account:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel, "2, 4, right, default");

		owningAccount = new JTextField();
		owningAccount.setEditable(false);
		contentPanel.add(owningAccount, "4, 4, fill, default");
		owningAccount.setColumns(10);

		lblAmount = new JLabel("Amount:");
		lblAmount.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAmount, "2, 6, right, default");

		amount = new JTextField();
		contentPanel.add(amount, "4, 6, fill, default");
		amount.getDocument().addDocumentListener(documentListener);
		amount.setColumns(10);

		lblNewLabel_1 = new JLabel("Frequency:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 8, right, default");

		frequency = new JComboBox<>();
		frequency.setEditable(false);
		frequency.addItemListener((event) -> {
			okButton.setEnabled(validFields());
		});
		contentPanel.add(frequency, "4, 8, fill, default");

		lblNewLabel_2 = new JLabel("Start Date:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 10, right, default");

		startDate = new JTextField();
		startDate.setEditable(false);
		contentPanel.add(startDate, "4, 10, fill, default");
		startDate.setColumns(10);

		lblNewLabel_3 = new JLabel("Recipient Account");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_3, "2, 12, right, default");

		recipientAccount = new JTextField();
		recipientAccount.setEditable(false);
		contentPanel.add(recipientAccount, "4, 12, fill, default");
		recipientAccount.setColumns(10);

		lblNewLabel_4 = new JLabel("Reference:");
		contentPanel.add(lblNewLabel_4, "2, 14, right, default");

		reference = new JTextField();
		contentPanel.add(reference, "4, 14, fill, default");
		reference.getDocument().addDocumentListener(documentListener);
		reference.setColumns(10);

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Update Standing Order");
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
					standingOrder = (StandingOrder) standingOrders.getSelectedItem();
					changedStandingOrder = new StandingOrder(standingOrder);
					changedStandingOrder.setAmount(new Money(amount.getText()));
					changedStandingOrder.setFrequency((Period) frequency.getSelectedItem());
					changedStandingOrder.setReference(reference.getText());
					result = OK_PRESSED;
					setVisible(false);
				} catch (IllegalArgumentException i) {
					JOptionPane.showMessageDialog(ChangeStandingOrderDialog.this,
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

	public StandingOrder changedStandingOrder() {
		LOGGER.entering(CLASS_NAME, "changedStandingOrder");
		LOGGER.exiting(CLASS_NAME, "changedStandingOrder", standingOrder);
		return changedStandingOrder;
	}

	private boolean validFields() {
		return validStandingOrder() && !emptyTextField(amount) && validFrequency() && !emptyTextField(reference);
	}

	private boolean emptyTextField(JTextField field) {
		return field.getText().isEmpty();
	}

	private boolean validStandingOrder() {
		return standingOrders.getSelectedIndex() >= 0;
	}

	private boolean validFrequency() {
		return frequency.getSelectedIndex() >= 0;
	}

	private void loadStandingOrders() {
		orders = BankMonitor.instance().standingOrders();
		Collections.sort(orders, (o1, o2) -> {
			return o1.reference().compareTo(o2.reference());
		});
		for (int i = 0; i < orders.size(); i++) {
			standingOrders.addItem(orders.get(i));
		}
		standingOrders.setSelectedIndex(-1);
	}

	private void loadFrequency() {
		for (Period period : Period.values()) {
			frequency.addItem(period);
		}
		frequency.setSelectedIndex(-1);
	}

	private void clearFields() {
		owningAccount.setText("");
		amount.setText("");
		startDate.setText("");
		frequency.setSelectedIndex(-1);
		recipientAccount.setText("");
		reference.setText("");
		okButton.setEnabled(validFields());
	}

	private void populateFields() {
		StandingOrder order = (StandingOrder) standingOrders.getSelectedItem();
		owningAccount.setText(order.owner().accountId().accountNumber());
		startDate.setText(order.nextActionDue().toString());
		amount.setText(order.amount().toString());
		frequency.setSelectedItem(order.frequency());
		recipientAccount.setText(order.recipient().accountId().accountNumber());
		reference.setText(order.reference());
	}

}
