package applications.bank.gui.modified;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import application.base.app.gui.ColoredPanel;
import application.base.app.gui.TopColoredPanel;
import application.definition.ApplicationConfiguration;
import application.model.Money;
import application.notification.Notification;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import applications.bank.gui.charts.PieChartComponent;
import applications.bank.gui.models.BankBalanceTableModel;
import applications.bank.model.Bank;
import applications.bank.model.Investment;
import applications.bank.storage.AccountNotificationType;
import applications.bank.storage.BankMonitor;
import applications.bank.storage.BankNotificationType;
import applications.bank.storage.InvestmentNotificationType;
import applications.bank.storage.TransactionNotificationType;

public class SummaryPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = SummaryPanel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private JTextField bankTotal;
	private JTextField investmentTotal;
	private JTextField grandTotal;

	private NotificationListener bankListener = (Notification notification) -> {
		LOGGER.entering(CLASS_NAME, "addRemoveNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "addRemoveRun");
			refresh();
			LOGGER.exiting(CLASS_NAME, "addRemoveRun");
		});
		LOGGER.exiting(CLASS_NAME, "addRemoveNotify");
	};

	private NotificationListener accountListener = (Notification notification) -> {
		LOGGER.entering(CLASS_NAME, "addRemoveNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "addRemoveRun");
			refresh();
			LOGGER.exiting(CLASS_NAME, "addRemoveRun");
		});
		LOGGER.exiting(CLASS_NAME, "addRemoveNotify");
	};

	private NotificationListener investmentListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addChangeRemoveNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "addChangeRemoveRun");
			refresh();
			LOGGER.exiting(CLASS_NAME, "addChangeRemoveRun");
		});
		LOGGER.exiting(CLASS_NAME, "addChangeRemoveNotify");
	};

	private NotificationListener transactionListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addChangeRemoveNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "addChangeRemoveRun");
			refresh();
			LOGGER.exiting(CLASS_NAME, "addChangeRemoveRun");
		});
		LOGGER.exiting(CLASS_NAME, "addChangeRemoveNotify");
	};

	/**
	 * Create the panel.
	 */
	public SummaryPanel() {
		LOGGER.entering(CLASS_NAME, "init");
		setLayout(new BorderLayout(0, 0));

		JPanel panel = new TopColoredPanel();
		add(panel, BorderLayout.NORTH);

		JLabel lblNewLabel = new JLabel("Summary");
		lblNewLabel.setFont(new Font("Century Schoolbook", Font.PLAIN, 18));
		panel.add(lblNewLabel);

		JPanel panel_1 = new ColoredPanel();
		panel_1.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lblNewLabel_1 = new JLabel("Balance in bank accounts");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setFont(new Font("Century Schoolbook", Font.PLAIN, 14));
		panel_1.add(lblNewLabel_1, "2, 2, right, default");

		bankTotal = new JTextField();
		bankTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		bankTotal.setFont(new Font("Century Schoolbook", Font.PLAIN, 14));
		bankTotal.setEditable(false);
		panel_1.add(bankTotal, "4, 2, fill, default");
		bankTotal.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Balance in Investments");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2.setFont(new Font("Century Schoolbook", Font.PLAIN, 14));
		panel_1.add(lblNewLabel_2, "2, 4, right, default");

		investmentTotal = new JTextField();
		investmentTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		investmentTotal.setFont(new Font("Century Schoolbook", Font.PLAIN, 14));
		investmentTotal.setEditable(false);
		panel_1.add(investmentTotal, "4, 4, fill, default");
		investmentTotal.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Total balance");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_3.setFont(new Font("Century Schoolbook", Font.PLAIN, 14));
		panel_1.add(lblNewLabel_3, "2, 6, right, default");

		grandTotal = new JTextField();
		grandTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		grandTotal.setFont(new Font("Century Schoolbook", Font.PLAIN, 14));
		grandTotal.setEditable(false);
		panel_1.add(grandTotal, "4, 6, fill, default");
		grandTotal.setColumns(10);

		refresh();

		BankBalanceTableModel model = new BankBalanceTableModel();
		PieChartComponent tc = new PieChartComponent(model);
		tc.setOpaque(false);
		tc.setPreferredSize(new Dimension(700, 300));
		tc.setMaximumSize(new Dimension(700, 300));
		panel_1.add(tc, "2,8,fill,default");
		ToolTipManager.sharedInstance().registerComponent(tc);

		addListeners();

		LOGGER.exiting(CLASS_NAME, "init");
	}

	private void refresh() {
		LOGGER.entering(CLASS_NAME, "refresh");

		Money bankBalance = new Money("0.00");
		Money investmentBalance = new Money("0.00");
		Money grandBalance = new Money("0.00");

		List<Bank> banks = BankMonitor.instance().banks();
		List<Investment> investments = BankMonitor.instance().investments();

		for (Bank bank : banks) {
			bankBalance = bankBalance.plus(bank.balance());
		}

		for (Investment investment : investments) {
			investmentBalance = investmentBalance.plus(investment.value());
		}

		grandBalance = grandBalance.plus(bankBalance);
		grandBalance = grandBalance.plus(investmentBalance);

		bankTotal.setText(bankBalance.cost());
		investmentTotal.setText(investmentBalance.cost());
		grandTotal.setText(grandBalance.cost());

		LOGGER.exiting(CLASS_NAME, "refresh");
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "addListeners");
		NotificationCentre.addListener(bankListener, BankNotificationType.Add, BankNotificationType.Removed);
		NotificationCentre.addListener(accountListener, AccountNotificationType.Removed);
		NotificationCentre.addListener(investmentListener, InvestmentNotificationType.Add,
				InvestmentNotificationType.Changed, InvestmentNotificationType.Removed);
		NotificationCentre.addListener(transactionListener, TransactionNotificationType.Add,
				TransactionNotificationType.Removed);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		NotificationCentre.removeListener(bankListener);
		NotificationCentre.removeListener(accountListener);
		NotificationCentre.removeListener(investmentListener);
		NotificationCentre.removeListener(transactionListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}
}
