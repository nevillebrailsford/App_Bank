package applications.bank.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Logger;

import javax.swing.JButton;
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
import application.model.Address;
import application.model.PostCode;
import application.model.SortCode;
import applications.bank.model.Bank;
import applications.bank.model.Branch;

public class AddBranchDialog extends JDialog {

	public static final int OK_PRESSED = 1;
	public static final int CANCEL_PRESSED = 0;
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = AddBranchDialog.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private final JPanel contentPanel;
	private JLabel instructions;
	private JButton okButton;
	private JButton cancelButton;
	private Branch branch = null;
	private int result = CANCEL_PRESSED;
	private JLabel lblSortCode;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JTextField town;
	private JLabel lblNewLabel_3;
	private JTextField county;
	private JTextField sortCode;
	private JTextField street;
	private JTextField bankName;
	private JLabel lblNewLabel_4;
	private JTextField postCode;

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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddBranchDialog dialog = new AddBranchDialog(null, null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddBranchDialog(JFrame parent, Bank bank) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Add a Branch");
		contentPanel = new ColoredPanel();
		getContentPane().setLayout(new BorderLayout());
		{
			instructions = new JLabel("Enter the details below to add a branch.");
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

		JLabel lblNewLabel = new JLabel("Bank Name:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel, "2, 2, right, default");

		bankName = new JTextField();
		bankName.setEditable(false);
		if (bank != null) {
			bankName.setText(bank.name());
		}
		contentPanel.add(bankName, "4, 2, fill, default");
		bankName.setColumns(10);

		lblSortCode = new JLabel("Sort Code:");
		lblSortCode.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblSortCode, "2, 4, right, default");

		sortCode = new JTextField();
		sortCode.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				town.setText("");
				okButton.setEnabled(false);
			}
		});
		sortCode.getDocument().addDocumentListener(documentListener);
		contentPanel.add(sortCode, "4, 4, fill, default");
		sortCode.setColumns(10);

		lblNewLabel_1 = new JLabel("Street:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1, "2, 6, right, default");

		street = new JTextField();
		street.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				town.setText("");
				okButton.setEnabled(false);
			}
		});
		street.getDocument().addDocumentListener(documentListener);
		contentPanel.add(street, "4, 6, fill, top");
		street.setColumns(10);

		lblNewLabel_2 = new JLabel("Town:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_2, "2, 8, right, default");

		town = new JTextField();
		town.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				town.setText("");
				okButton.setEnabled(false);
			}
		});
		town.getDocument().addDocumentListener(documentListener);
		contentPanel.add(town, "4, 8, fill, default");
		town.setColumns(10);

		lblNewLabel_3 = new JLabel("County:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_3, "2, 10, right, default");

		county = new JTextField();
		county.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				county.setText("");
				okButton.setEnabled(false);
			}
		});
		county.getDocument().addDocumentListener(documentListener);
		contentPanel.add(county, "4, 10, fill, default");
		county.setColumns(10);

		lblNewLabel_4 = new JLabel("Post Code:");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_4, "2, 12, right, default");

		postCode = new JTextField();
		postCode.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				postCode.setText("");
				okButton.setEnabled(false);
			}
		});
		postCode.getDocument().addDocumentListener(documentListener);
		contentPanel.add(postCode, "4, 12, fill, default");
		postCode.setColumns(10);

		JPanel buttonPane = new BottomColoredPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			okButton = new JButton("Add Branch");
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
					SortCode sc = new SortCode(sortCode.getText());
					PostCode pc = new PostCode(postCode.getText());
					Address address = new Address.Builder().street(street.getText()).town(town.getText())
							.county(county.getText()).postCode(pc).build();
					branch = new Branch.Builder().bank(bank).sortCode(sc).address(address).build();
					result = OK_PRESSED;
					setVisible(false);
				} catch (IllegalArgumentException i) {
					JOptionPane.showMessageDialog(AddBranchDialog.this, "Error has occured: " + i.getMessage(),
							"Error occured", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		okButton.setEnabled(false);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				branch = null;
				result = CANCEL_PRESSED;
				setVisible(false);
			}
		});
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

	public Branch branch() {
		LOGGER.entering(CLASS_NAME, "branch");
		LOGGER.exiting(CLASS_NAME, "branch", branch);
		return branch;
	}

	private boolean validFields() {
		return validSortCode() && !emptyTextField(street) && !emptyTextField(town) && !emptyTextField(county)
				&& validPostCode();
	}

	private boolean emptyTextField(JTextField field) {
		return field.getText().isEmpty();
	}

	private boolean validSortCode() {
		if (emptyTextField(sortCode)) {
			return false;
		}
		String sc = sortCode.getText();
		if (sc.matches(SortCode.sortCodeRegularExpression)) {
			return true;
		}
		return false;
	}

	private boolean validPostCode() {
		if (emptyTextField(postCode)) {
			return false;
		}
		String pc = postCode.getText().toUpperCase();
		if (pc.matches(PostCode.postCodeRegularExpression)) {
			return true;
		}
		return false;
	}
}
