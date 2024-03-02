package applications.bank.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import application.base.app.ApplicationBaseForGUI;
import application.base.app.Parameters;
import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColorProvider;
import application.base.app.gui.PreferencesDialog;
import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.inifile.IniFile;
import application.notification.Notification;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import application.report.ReportNotificationType;
import application.storage.StoreDetails;
import application.thread.ThreadServices;
import application.timer.TimerService;
import applications.bank.gui.BankApplicationMenu;
import applications.bank.gui.BankGUIConstants;
import applications.bank.gui.IApplication;
import applications.bank.gui.TimerHandler;
import applications.bank.gui.actions.BankActionFactory;
import applications.bank.gui.changes.AddAccountChange;
import applications.bank.gui.changes.AddBankChange;
import applications.bank.gui.changes.AddInvestmentChange;
import applications.bank.gui.changes.AddStandingOrderChange;
import applications.bank.gui.changes.AddTransactionChange;
import applications.bank.gui.changes.ChangeInvestmentChange;
import applications.bank.gui.changes.ChangeStandingOrderChange;
import applications.bank.gui.changes.RemoveAccountChange;
import applications.bank.gui.changes.RemoveBankChange;
import applications.bank.gui.changes.RemoveInvestmentChange;
import applications.bank.gui.changes.RemoveStandingOrderChange;
import applications.bank.gui.changes.TransferChange;
import applications.bank.gui.charts.LineChartComponent;
import applications.bank.gui.charts.LineChartPopup;
import applications.bank.gui.charts.PieChartComponent;
import applications.bank.gui.charts.PieChartPopup;
import applications.bank.gui.dialogs.AddAccountDialog;
import applications.bank.gui.dialogs.AddBankDialog;
import applications.bank.gui.dialogs.AddInvestmentDialog;
import applications.bank.gui.dialogs.AddStandingOrderDialog;
import applications.bank.gui.dialogs.ChangeInvestmentDialog;
import applications.bank.gui.dialogs.ChangeStandingOrderDialog;
import applications.bank.gui.dialogs.PayMoneyInDialog;
import applications.bank.gui.dialogs.PaySomeoneDialog;
import applications.bank.gui.dialogs.RemoveAccountDialog;
import applications.bank.gui.dialogs.RemoveBankDialog;
import applications.bank.gui.dialogs.RemoveInvestmentDialog;
import applications.bank.gui.dialogs.RemoveStandingOrderDialog;
import applications.bank.gui.dialogs.TransferDialog;
import applications.bank.gui.dialogs.ViewStandingOrdersDialog;
import applications.bank.gui.dialogs.ViewTransactionsDialog;
import applications.bank.gui.models.AccountBalanceHistoryTableModel;
import applications.bank.gui.models.BankPercentagesTableModel;
import applications.bank.gui.models.HistoryTableModel;
import applications.bank.gui.models.InvestmentPercentagesTableModel;
import applications.bank.gui.models.TotalHistoryTableModel;
import applications.bank.gui.modified.BankPanel;
import applications.bank.gui.modified.InvestmentPanel;
import applications.bank.gui.modified.MainBankTabbedPane;
import applications.bank.model.Account;
import applications.bank.model.Bank;
import applications.bank.model.Investment;
import applications.bank.model.StandingOrder;
import applications.bank.model.Transaction;
import applications.bank.model.Transfer;
import applications.bank.report.BankingReport;
import applications.bank.storage.BankMonitor;
import applications.bank.storage.BankNotificationType;
import applications.bank.storage.BankRead;
import applications.bank.storage.InvestmentNotificationType;
import applications.bank.storage.StandingOrderNotificationType;

public class BankApplication extends ApplicationBaseForGUI implements IApplication {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = BankApplication.class.getName();
	private static Logger LOGGER = null;

	private MainBankTabbedPane mainPanel = null;
	private BankActionFactory actionFactory;
	private BankApplicationMenu menuBar;
	private JFrame parent;
	private JButton exit;
	private TimerHandler timerHandler;

	private NotificationListener bankAddedListener = (Notification notification) -> {
		LOGGER.entering(CLASS_NAME, "addNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "addRun");
			addBankNotification(notification);
			LOGGER.exiting(CLASS_NAME, "addRun");
		});
		LOGGER.exiting(CLASS_NAME, "addNotify");
	};

	private NotificationListener bankRemovedListener = (Notification notification) -> {
		LOGGER.entering(CLASS_NAME, "removeNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "removeRun");
			removeBankNotification(notification);
			LOGGER.exiting(CLASS_NAME, "removeRun");
		});
		LOGGER.exiting(CLASS_NAME, "removeNotify");
	};

	private NotificationListener addInvestmentListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "removeRun");
			addInvestmentNotification(notification);
			LOGGER.exiting(CLASS_NAME, "removeRun");
		});
		LOGGER.exiting(CLASS_NAME, "addNotify");
	};

	private NotificationListener removeInvestmentListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "removeNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "removeRun");
			removeInvestmentNotification(notification);
			LOGGER.exiting(CLASS_NAME, "removeRun");
		});
		LOGGER.exiting(CLASS_NAME, "removeNotify");
	};

	private NotificationListener addStandingOrderListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "addNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "addRun");
			updateMenuItemStatus();
			JOptionPane.showMessageDialog(parent, "Standing Order has been added", "Status Report",
					JOptionPane.INFORMATION_MESSAGE);
			LOGGER.exiting(CLASS_NAME, "addRun");
		});
		LOGGER.exiting(CLASS_NAME, "addNotify");
	};

	private NotificationListener changeStandingOrderListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "changeNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "changeRun");
			updateMenuItemStatus();
			JOptionPane.showMessageDialog(parent, "Standing Order has been changed", "Status Report",
					JOptionPane.INFORMATION_MESSAGE);
			LOGGER.exiting(CLASS_NAME, "changeRun");
		});
		LOGGER.exiting(CLASS_NAME, "changeNotify");
	};

	private NotificationListener removeStandingOrderListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "removeNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "removeRun");
			updateMenuItemStatus();
			JOptionPane.showMessageDialog(parent, "Standing Order has been removed", "Status Report",
					JOptionPane.INFORMATION_MESSAGE);
			LOGGER.exiting(CLASS_NAME, "removeRun");
		});
		LOGGER.exiting(CLASS_NAME, "removeNotify");
	};

	private NotificationListener reportListener = (notification) -> {
		LOGGER.entering(CLASS_NAME, "reportNotify", notification);
		SwingUtilities.invokeLater(() -> {
			LOGGER.entering(CLASS_NAME, "reportRun");
			if (notification.notificationType().equals(ReportNotificationType.Created)) {
				JOptionPane.showMessageDialog(parent,
						"Report " + notification.subject().get() + " has been created successfully", "Status Report",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(parent, "Unable to create report", "Status Report",
						JOptionPane.INFORMATION_MESSAGE);
			}
			LOGGER.exiting(CLASS_NAME, "reportRun");
		});
		LOGGER.exiting(CLASS_NAME, "reportNotify");
	};

	public BankApplication() {
	}

	@Override
	public void preferencesAction() {
		LOGGER.entering(CLASS_NAME, "preferencesAction");
		PreferencesDialog dialog = new PreferencesDialog(parent);
		dialog.setVisible(true);
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "preferencesAction");
	}

	@Override
	public void undoAction() {
		LOGGER.entering(CLASS_NAME, "undoAction");
		ThreadServices.instance().executor().submit(() -> {
			ChangeManager.instance().undo();
		});
		LOGGER.exiting(CLASS_NAME, "undoAction");
	}

	@Override
	public void redoAction() {
		LOGGER.entering(CLASS_NAME, "redoAction");
		ThreadServices.instance().executor().submit(() -> {
			ChangeManager.instance().redo();
		});
		LOGGER.exiting(CLASS_NAME, "redoAction");
	}

	@Override
	public void exitApplicationAction() {
		LOGGER.entering(CLASS_NAME, "exitApplicationAction");
		try {
			shutdown();
		} catch (Exception e) {
		}
		LOGGER.exiting(CLASS_NAME, "exitApplicationAction");
	}

	@Override
	public void printAction() {
		LOGGER.entering(CLASS_NAME, "printAction");
		ThreadServices.instance().executor().execute(
				new BankingReport(ApplicationConfiguration.applicationDefinition().applicationName() + ".report.pdf"));
		LOGGER.exiting(CLASS_NAME, "printAction");
	}

	@Override
	public void addBankAction() {
		LOGGER.entering(CLASS_NAME, "addBankAction");
		AddBankDialog dialog = new AddBankDialog(parent);
		int result = dialog.displayAndWait();
		if (result == AddBankDialog.OK_PRESSED) {
			Bank newBank = dialog.bank();
			AddBankChange newBankChange = new AddBankChange(newBank);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(newBankChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "addBankAction");
	}

	@Override
	public void removeBankAction() {
		LOGGER.entering(CLASS_NAME, "removeBankAction");
		RemoveBankDialog dialog = new RemoveBankDialog(parent);
		int result = dialog.displayAndWait();
		if (result == RemoveBankDialog.OK_PRESSED) {
			Bank oldBank = dialog.bank();
			RemoveBankChange removeBankChange = new RemoveBankChange(oldBank);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(removeBankChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "removeBankAction");
	}

	@Override
	public void addAccountAction() {
		LOGGER.entering(CLASS_NAME, "addAccountAction");
		AddAccountDialog dialog = new AddAccountDialog(parent);
		int result = dialog.displayAndWait();
		if (result == AddAccountDialog.OK_PRESSED) {
			Account newAccount = dialog.account();
			AddAccountChange newAccountChange = new AddAccountChange(newAccount);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(newAccountChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "addBankAction");
	}

	@Override
	public void removeAccountAction() {
		LOGGER.entering(CLASS_NAME, "removeAccountAction");
		RemoveAccountDialog dialog = new RemoveAccountDialog(parent);
		int result = dialog.displayAndWait();
		if (result == RemoveAccountDialog.OK_PRESSED) {
			Account oldAccount = dialog.account();
			RemoveAccountChange removeAccountChange = new RemoveAccountChange(oldAccount);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(removeAccountChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "removeAccountAction");
	}

	@Override
	public void addInvestmentAction() {
		LOGGER.entering(CLASS_NAME, "addInvestmentAction");
		AddInvestmentDialog dialog = new AddInvestmentDialog(parent);
		int result = dialog.displayAndWait();
		if (result == AddInvestmentDialog.OK_PRESSED) {
			Investment newInvestment = dialog.investment();
			AddInvestmentChange addInvestmentChange = new AddInvestmentChange(newInvestment);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(addInvestmentChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "addInvestmentAction");
	}

	@Override
	public void changeInvestmentAction() {
		LOGGER.entering(CLASS_NAME, "changeInvestmentAction");
		ChangeInvestmentDialog dialog = new ChangeInvestmentDialog(parent);
		Investment thisInvestment = BankMonitor.instance().findInvestment(mainPanel.selectedInvestment());
		if (thisInvestment != null) {
			dialog.setInvestment(thisInvestment);
		}
		int result = dialog.displayAndWait();
		if (result == ChangeInvestmentDialog.OK_PRESSED) {
			Investment oldInvestment = dialog.oldInvestment();
			Investment investment = dialog.investment();
			ChangeInvestmentChange changeInvestmentChange = new ChangeInvestmentChange(oldInvestment, investment);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(changeInvestmentChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "changeInvestmentAction");
	}

	@Override
	public void removeInvestmentAction() {
		LOGGER.entering(CLASS_NAME, "removeInvestmentAction");
		RemoveInvestmentDialog dialog = new RemoveInvestmentDialog(parent);
		int result = dialog.displayAndWait();
		if (result == ChangeInvestmentDialog.OK_PRESSED) {
			Investment investment = dialog.investment();
			RemoveInvestmentChange removeInvestmentChange = new RemoveInvestmentChange(investment);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(removeInvestmentChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "removeInvestmentAction");
	}

	@Override
	public void addStandingOrderAction() {
		LOGGER.entering(CLASS_NAME, "addStandingOrderAction");
		AddStandingOrderDialog dialog = new AddStandingOrderDialog(parent);
		int result = dialog.displayAndWait();
		if (result == AddStandingOrderDialog.OK_PRESSED) {
			StandingOrder standingOrder = dialog.standingOrder();
			AddStandingOrderChange addStandingOrderChange = new AddStandingOrderChange(standingOrder);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(addStandingOrderChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "addStandingOrderAction");
	}

	@Override
	public void changeStandingOrderAction() {
		LOGGER.entering(CLASS_NAME, "changeStandingOrderAction");
		ChangeStandingOrderDialog dialog = new ChangeStandingOrderDialog(parent);
		int result = dialog.displayAndWait();
		if (result == ChangeStandingOrderDialog.OK_PRESSED) {
			StandingOrder oldStandingOrder = dialog.standingOrder();
			StandingOrder changedStandingOrder = dialog.changedStandingOrder();
			ChangeStandingOrderChange changeStandingOrderChange = new ChangeStandingOrderChange(oldStandingOrder,
					changedStandingOrder);
			ThreadServices.instance().executor().submit(() -> {
				try {
					ChangeManager.instance().execute(changeStandingOrderChange);
				} catch (Throwable t) {
					System.out.println(t);
				}
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "changeStandingOrderAction");
	}

	@Override
	public void removeStandingOrderAction() {
		LOGGER.entering(CLASS_NAME, "removeStandingOrderAction");
		RemoveStandingOrderDialog dialog = new RemoveStandingOrderDialog(parent);
		int result = dialog.displayAndWait();
		if (result == RemoveStandingOrderDialog.OK_PRESSED) {
			StandingOrder standingOrder = dialog.standingOrder();
			RemoveStandingOrderChange removeStandingOrderChange = new RemoveStandingOrderChange(standingOrder);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(removeStandingOrderChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "removeStandingOrderAction");
	}

	@Override
	public void payMoneyInAction() {
		LOGGER.entering(CLASS_NAME, "payMoneyInAction");
		PayMoneyInDialog dialog = new PayMoneyInDialog(parent);
		Account account = mainPanel.selectedAccount();
		if (account != null) {
			dialog.setAccount(account);
		}
		int result = dialog.displayAndWait();
		if (result == PayMoneyInDialog.OK_PRESSED) {
			Transaction transaction = dialog.transaction();
			AddTransactionChange addTransactionChange = new AddTransactionChange(transaction);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(addTransactionChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "payMoneyInAction");
	}

	@Override
	public void paySomeoneAction() {
		LOGGER.entering(CLASS_NAME, "paySomeoneAction");
		PaySomeoneDialog dialog = new PaySomeoneDialog(parent);
		Account account = mainPanel.selectedAccount();
		if (account != null) {
			dialog.setAccount(account);
		}
		int result = dialog.displayAndWait();
		if (result == PaySomeoneDialog.OK_PRESSED) {
			Transaction transaction = dialog.transaction();
			AddTransactionChange addTransactionChange = new AddTransactionChange(transaction);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(addTransactionChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "paySomeoneAction");
	}

	@Override
	public void transferAction() {
		LOGGER.entering(CLASS_NAME, "transferAction");
		TransferDialog dialog = new TransferDialog(parent);
		Account account = mainPanel.selectedAccount();
		if (account != null) {
			dialog.setAccount(account);
		}
		int result = dialog.displayAndWait();
		if (result == TransferDialog.OK_PRESSED) {
			Transfer transfer = dialog.transfer();
			TransferChange transferChange = new TransferChange(transfer);
			ThreadServices.instance().executor().submit(() -> {
				ChangeManager.instance().execute(transferChange);
			});
		}
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "transferAction");
	}

	@Override
	public void viewTransactionsAction() {
		LOGGER.entering(CLASS_NAME, "viewTransactionsAction");
		Account account = mainPanel.selectedAccount();
		if (account != null) {
			account = BankMonitor.instance().findAccount(account);
			ViewTransactionsDialog dialog = new ViewTransactionsDialog(parent, account);
			dialog.setVisible(true);
			dialog.dispose();
		}
		LOGGER.exiting(CLASS_NAME, "viewTransactionsAction");
	}

	@Override
	public void viewStandingOrdersAction() {
		LOGGER.entering(CLASS_NAME, "viewStandingOrdersAction");
		Account account = BankMonitor.instance().findAccount(mainPanel.selectedAccount());
		ViewStandingOrdersDialog dialog = new ViewStandingOrdersDialog(parent, account);
		dialog.setVisible(true);
		dialog.dispose();
		LOGGER.exiting(CLASS_NAME, "viewStandingOrderAction");
	}

	@Override
	public void viewBankPercentagesAction() {
		LOGGER.entering(CLASS_NAME, "viewBankPercentagesAction");
		BankPercentagesTableModel model = new BankPercentagesTableModel();
		PieChartComponent tc = new PieChartComponent(model);
		ToolTipManager.sharedInstance().registerComponent(tc);
		PieChartPopup tcp = new PieChartPopup(model, "Bank Dashboard");
		tcp.setVisible(true);
		LOGGER.exiting(CLASS_NAME, "viewBankPercentagesAction");
	}

	@Override
	public void viewBankBalanceHistoryAction() {
		LOGGER.entering(CLASS_NAME, "viewBankBalanceHistoryAction");
		Account account = BankMonitor.instance().findAccount(mainPanel.selectedAccount());
		AccountBalanceHistoryTableModel model = new AccountBalanceHistoryTableModel(account);
		LineChartComponent tc = new LineChartComponent(model);
		ToolTipManager.sharedInstance().registerComponent(tc);
		LineChartPopup lcp = new LineChartPopup(model, "Bank Balance history");
		lcp.setVisible(true);
		LOGGER.exiting(CLASS_NAME, "viewBankBalanceHistoryAction");
	}

	@Override
	public void viewInvestmentPercentagesAction() {
		LOGGER.entering(CLASS_NAME, "viewInvestmentPercentagesAction");
		InvestmentPercentagesTableModel model = new InvestmentPercentagesTableModel();
		PieChartComponent tc = new PieChartComponent(model);
		ToolTipManager.sharedInstance().registerComponent(tc);
		PieChartPopup tcp = new PieChartPopup(model, "Investment Dashboard");
		tcp.setVisible(true);
		LOGGER.exiting(CLASS_NAME, "viewInvestmentPercentagesAction");
	}

	@Override
	public void viewInvestmentHistoryAction() {
		LOGGER.entering(CLASS_NAME, "viewInvestmentHistory");
		Investment investment = BankMonitor.instance().findInvestment(mainPanel.selectedInvestment());
		HistoryTableModel model = new HistoryTableModel(investment);
		LineChartComponent tc = new LineChartComponent(model);
		ToolTipManager.sharedInstance().registerComponent(tc);
		LineChartPopup lcp = new LineChartPopup(model, "Investment history");
		lcp.setVisible(true);
		LOGGER.exiting(CLASS_NAME, "viewInvestmentHistory");
	}

	@Override
	public void viewTotalInvestmentHistoryAction() {
		LOGGER.entering(CLASS_NAME, "viewTotalInvestmentHistoryAction");
		TotalHistoryTableModel model = new TotalHistoryTableModel(BankMonitor.instance().investments());
		LineChartComponent tc = new LineChartComponent(model);
		ToolTipManager.sharedInstance().registerComponent(tc);
		LineChartPopup lcp = new LineChartPopup(model, "Investment history");
		lcp.setVisible(true);
		LOGGER.exiting(CLASS_NAME, "viewTotalInvestmentHistoryAction");
	}

	@Override
	public void helpAboutAction() {
		LOGGER.entering(CLASS_NAME, "helpAboutAction");
		String applicationName = ApplicationConfiguration.applicationDefinition().applicationName();
		String title = "About " + applicationName;
		String message = getBuildInformation(applicationName);
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		LOGGER.exiting(CLASS_NAME, "helpAboutAction");
	}

	@Override
	public void configureStoreDetails() {
		dataLoader = new BankRead();
		storeDetails = new StoreDetails(dataLoader, Constants.MODEL, Constants.BANK_FILE);
	}

	@Override
	public ApplicationDefinition createApplicationDefinition(Parameters parameters) {
		ApplicationDefinition definition = new ApplicationDefinition(parameters.getNamed().get("name")) {

			@Override
			public Optional<Color> bottomColor() {
				String bottom = IniFile.value(BankGUIConstants.BOTTOM_COLOR);
				if (bottom.isEmpty() || bottom.equals("default")) {
					bottom = "lightgreen";
					IniFile.store(BankGUIConstants.BOTTOM_COLOR, bottom);
				}
				Color bottomColor = ColorProvider.get(bottom);
				return Optional.ofNullable(bottomColor);
			}

			@Override
			public Optional<Color> topColor() {
				String top = IniFile.value(BankGUIConstants.TOP_COLOR);
				if (top.isEmpty() || top.equals("default")) {
					top = "indianred";
					IniFile.store(BankGUIConstants.TOP_COLOR, top);
				}
				Color topColor = ColorProvider.get(top);
				return Optional.ofNullable(topColor);
			}
		};
		return definition;
	}

	@Override
	public void start(JFrame parent) {
		LOGGER = ApplicationConfiguration.logger();
		LOGGER.entering(CLASS_NAME, "start");
		System.out.println(
				"Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is starting");
		actionFactory = BankActionFactory.instance(this);
		this.parent = parent;
		menuBar = new BankApplicationMenu(this);
		mainPanel = new MainBankTabbedPane(menuBar, this);
		Dimension size = new Dimension(BankPanel.WIDTH, BankPanel.HEIGHT);
		mainPanel.setMaximumSize(size);
		mainPanel.setMinimumSize(size);
		mainPanel.setPreferredSize(size);
		parent.setJMenuBar(menuBar);
		parent.add(mainPanel, BorderLayout.CENTER);
		JPanel statusPanel = new BottomColoredPanel();
		statusPanel.setLayout(new FlowLayout());
		exit = new JButton(actionFactory.exitAction());
		statusPanel.add(exit);
		parent.add(statusPanel, BorderLayout.PAGE_END);
		pack();
		parent.setResizable(false);
		updateMenuItemStatus();
		addListeners();
		createTimer();
		TimerService.instance().start();
		LOGGER.exiting(CLASS_NAME, "start");
	}

	@Override
	public void terminate() {
		LOGGER.entering(CLASS_NAME, "terminate");
		removeListeners();
		TimerService.instance().stop();
		System.out.println("Application " + ApplicationConfiguration.applicationDefinition().applicationName()
				+ " is terminating");
		LOGGER.exiting(CLASS_NAME, "terminate");
	}

	public static void main(String[] args) {
		System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		launch(args);
	}

	private void updateMenuItemStatus() {
		menuBar.enableUndo(ChangeManager.instance().undoable());
		menuBar.enableRedo(ChangeManager.instance().redoable());
	}

	private void addBankTab(Bank bank) {
		LOGGER.entering(CLASS_NAME, "addBankTab", bank);
		BankPanel bankPane = new BankPanel(bank, menuBar, this);
		mainPanel.bankTabbedPane().addTab(bank.name(), bankPane);
		updateMenuItemStatus();
		LOGGER.exiting(CLASS_NAME, "addBankTab");
	}

	private void addInvestmentTab(Investment investment) {
		LOGGER.entering(CLASS_NAME, "addInvestmentTab", investment);
		InvestmentPanel investmentPane = new InvestmentPanel(investment, menuBar, this);
		mainPanel.investmentTabbedPane().addTab(investment.name(), investmentPane);
		;
		LOGGER.exiting(CLASS_NAME, "addInvestmentTab");
	}

	private void removeBankTab(Bank bank) {
		LOGGER.entering(CLASS_NAME, "removeBankTab", bank);
		for (int i = 0; i < mainPanel.bankTabbedPane().getTabCount(); i++) {
			BankPanel bankPane = (BankPanel) mainPanel.bankTabbedPane().getComponentAt(i);
			if (bankPane.owns(bank)) {
				bankPane.removeListeners();
				mainPanel.bankTabbedPane().remove(i);
				break;
			}
		}
		updateMenuItemStatus();
		LOGGER.exiting(CLASS_NAME, "removeBankTab");
	}

	private void removeInvestmentTab(Investment investment) {
		LOGGER.entering(CLASS_NAME, "removeInvestmentTab", investment);
		for (int i = 0; i < mainPanel.bankTabbedPane().getTabCount(); i++) {
			InvestmentPanel investmentPane = (InvestmentPanel) mainPanel.investmentTabbedPane().getComponentAt(i);
			if (investmentPane.owns(investment)) {
				investmentPane.removeListeners();
				mainPanel.investmentTabbedPane().remove(i);
				break;
			}
		}
		updateMenuItemStatus();
		LOGGER.exiting(CLASS_NAME, "removeInvestmentTab");
	}

	private String getBuildInformation(String applicationName) {
		LOGGER.entering(CLASS_NAME, "getBuildInformation", applicationName);
		String result = "";
		StringBuilder builder = new StringBuilder(applicationName);
		try {
			builder.append("\nBuild: ").append(ApplicationDefinition.getFromManifest("Build-Number", getClass())
					.orElse("Could not be determined"));
			builder.append("\nBuild Date: ").append(
					ApplicationDefinition.getFromManifest("Build-Date", getClass()).orElse("Could not be determined"));
		} catch (Exception e) {
			builder.append("\nUnable to gather build version and date information\ndue to exception " + e.getMessage());
			LOGGER.fine("Caught exception: " + e.getMessage());
		}
		result = builder.toString();
		LOGGER.exiting(CLASS_NAME, "getBuildInformation", result);
		return result;
	}

	private void addListeners() {
		NotificationCentre.addListener(bankAddedListener, BankNotificationType.Add);
		NotificationCentre.addListener(bankRemovedListener, BankNotificationType.Removed);
		NotificationCentre.addListener(addInvestmentListener, InvestmentNotificationType.Add);
		NotificationCentre.addListener(removeInvestmentListener, InvestmentNotificationType.Removed);
		NotificationCentre.addListener(addStandingOrderListener, StandingOrderNotificationType.Add);
		NotificationCentre.addListener(changeStandingOrderListener, StandingOrderNotificationType.Changed);
		NotificationCentre.addListener(removeStandingOrderListener, StandingOrderNotificationType.Removed);
		NotificationCentre.addListener(reportListener, ReportNotificationType.Created, ReportNotificationType.Failed);
	}

	private void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		mainPanel.removeListeners();
		timerHandler.removeListeners();
		NotificationCentre.removeListener(bankAddedListener);
		NotificationCentre.removeListener(bankRemovedListener);
		NotificationCentre.removeListener(addInvestmentListener);
		NotificationCentre.removeListener(removeInvestmentListener);
		NotificationCentre.removeListener(addStandingOrderListener);
		NotificationCentre.removeListener(changeStandingOrderListener);
		NotificationCentre.removeListener(removeStandingOrderListener);
		NotificationCentre.removeListener(reportListener);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

	private void createTimer() {
		LOGGER.entering(CLASS_NAME, "createTimer");
		timerHandler = new TimerHandler();
		LOGGER.exiting(CLASS_NAME, "createTimer");
	}

	private void addBankNotification(Notification notification) {
		LOGGER.entering(CLASS_NAME, "addPropertyNotification", notification);
		Bank b = (Bank) notification.subject().get();
		addBankTab(b);
		updateMenuItemStatus();
		LOGGER.exiting(CLASS_NAME, "addPropertyNotification");
	}

	private void removeBankNotification(Notification notification) {
		LOGGER.entering(CLASS_NAME, "removeBankNotification", notification);
		Bank b = (Bank) notification.subject().get();
		removeBankTab(b);
		updateMenuItemStatus();
		LOGGER.exiting(CLASS_NAME, "removeBankNotification");
	}

	private void addInvestmentNotification(Notification notification) {
		LOGGER.entering(CLASS_NAME, "addInvestmentNotification", notification);
		Investment i = (Investment) notification.subject().get();
		addInvestmentTab(i);
		updateMenuItemStatus();
		LOGGER.exiting(CLASS_NAME, "addInvestmentNotification");
	}

	private void removeInvestmentNotification(Notification notification) {
		LOGGER.entering(CLASS_NAME, "removeInvestmentNotification", notification);
		Investment i = (Investment) notification.subject().get();
		removeInvestmentTab(i);
		updateMenuItemStatus();
		LOGGER.exiting(CLASS_NAME, "removeInvestmentNotification");
	}

}
