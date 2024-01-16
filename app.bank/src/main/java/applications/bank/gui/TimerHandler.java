package applications.bank.gui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.inifile.IniFile;
import application.mail.MailSender;
import application.notification.Notification;
import application.notification.NotificationCentre;
import application.notification.NotificationListener;
import application.thread.ThreadServices;
import application.timer.TimerNotificationType;
import applications.bank.gui.changes.TransferChange;
import applications.bank.model.StandingOrder;
import applications.bank.model.Transfer;
import applications.bank.storage.BankMonitor;

public class TimerHandler implements NotificationListener {
	private static final String CLASS_NAME = TimerHandler.class.getName();
	private static final Logger LOGGER = ApplicationConfiguration.logger();

	public TimerHandler() {
		LOGGER.entering(CLASS_NAME, "init");
		addListeners();
		LOGGER.exiting(CLASS_NAME, "init");
	}

	@Override
	public void notify(Notification notification) {
		LOGGER.entering(CLASS_NAME, "notify", notification);
		SwingUtilities.invokeLater(() -> {
			handleTimerNotification();
		});
		LOGGER.exiting(CLASS_NAME, "notify");
	}

	private void addListeners() {
		LOGGER.entering(CLASS_NAME, "addListeners");
		NotificationCentre.addListener(this, TimerNotificationType.Ticked);
		LOGGER.exiting(CLASS_NAME, "addListeners");
	}

	public void removeListeners() {
		LOGGER.entering(CLASS_NAME, "removeListeners");
		NotificationCentre.removeListener(this);
		LOGGER.exiting(CLASS_NAME, "removeListeners");
	}

	private void handleTimerNotification() {
		LOGGER.entering(CLASS_NAME, "handleTimerNotification");
		LocalDateTime now = LocalDateTime.now();
		String lastTime = IniFile.value(GUIConstants.LAST_TIME);
		if (lastTime.isEmpty()) {
			lastTime = now.toString();
			performTimedActions(now);
		}
		LocalDateTime previous = LocalDateTime.parse(lastTime);
		if (previous.plusDays(1).isBefore(now)) {
			performTimedActions(now);
		}
		LOGGER.exiting(CLASS_NAME, "handleTimerNotification");
	}

	private void performTimedActions(LocalDateTime now) {
		LOGGER.entering(CLASS_NAME, "performTimedActions", now);
		payOverdueStandingOrders(now);
		sendEmailIfRequired();
		updateLastTime(now);
		LOGGER.exiting(CLASS_NAME, "performTimedActions");
	}

	private void sendEmailIfRequired() {
		LOGGER.entering(CLASS_NAME, "sendEmailIfRequired");
		if (Boolean.valueOf(IniFile.value(GUIConstants.EMAIL_NOTIFICATION)).booleanValue()) {
			LOGGER.fine("Email notification is enabled");
			LocalDate lastSent;
			if (IniFile.value(GUIConstants.DATE_OF_LAST_EMAIL).trim().isEmpty()) {
				lastSent = LocalDate.now().minusDays(1);
			} else {
				lastSent = LocalDate.parse(IniFile.value(GUIConstants.DATE_OF_LAST_EMAIL));
			}
			sendEmail();
			updateDateOfLastEmailCheck();
			LOGGER.fine("lastSent = " + lastSent.toString());
		} else {
			LOGGER.fine("Email notification is not enabled");
		}
		LOGGER.exiting(CLASS_NAME, "sendEmailIfRequired");
	}

	private void sendEmail() {
		LOGGER.entering(CLASS_NAME, "sendEmail");
		String message = composeMessage();
		MailSender worker = new MailSender(message);
		ThreadServices.instance().executor().execute(worker);
		LOGGER.exiting(CLASS_NAME, "sendEmail");
	}

	private String composeMessage() {
		LOGGER.entering(CLASS_NAME, "composeMessage");
		StringBuffer message = new StringBuffer();
		message.append("Nothing to report");
		LOGGER.exiting(CLASS_NAME, "composeMessage", message.toString());
		return message.toString();
	}

	private void updateDateOfLastEmailCheck() {
		LOGGER.entering(CLASS_NAME, "updateDateOfLastEmailCheck");
		IniFile.store(GUIConstants.DATE_OF_LAST_EMAIL, LocalDate.now().toString());
		LOGGER.exiting(CLASS_NAME, "updateDateOfLastEmailCheck");
	}

	private void updateLastTime(LocalDateTime now) {
		LOGGER.entering(CLASS_NAME, "updateLastTime", now);
		IniFile.store(GUIConstants.LAST_TIME, now.toString());
		LOGGER.exiting(CLASS_NAME, "updateLastTime");
	}

	private void payOverdueStandingOrders(LocalDateTime now) {
		LOGGER.entering(CLASS_NAME, "payOverdueStandingOrders", now);
		LocalDate date = now.toLocalDate();
		for (StandingOrder order : BankMonitor.instance().standingOrders()) {
			if (order.nextActionDue().isBefore(date)) {
				processStandingOrder(order);
			}
		}
		LOGGER.exiting(CLASS_NAME, "payOverdueStandingOrders");
	}

	private void processStandingOrder(StandingOrder order) {
		LOGGER.entering(CLASS_NAME, "processStandingOrder", order);
		Transfer transfer = buildTransfer(order);
		processTransfer(transfer);
		updateStandingOrder(order);
		LOGGER.exiting(CLASS_NAME, "processStandingOrder");
	}

	private Transfer buildTransfer(StandingOrder order) {
		LOGGER.entering(CLASS_NAME, "buildTransfer", order);
		Transfer transfer = new Transfer(order.owner(), order.recipient(), order.amount());
		LOGGER.exiting(CLASS_NAME, "buildTransfer", transfer);
		return transfer;
	}

	private void processTransfer(Transfer transfer) {
		LOGGER.entering(CLASS_NAME, "processTransfer", transfer);
		TransferChange transferChange = new TransferChange(transfer);
		ThreadServices.instance().executor().submit(() -> {
			ChangeManager.instance().execute(transferChange);
		});
		LOGGER.exiting(CLASS_NAME, "processTransfer");
	}

	private void updateStandingOrder(StandingOrder order) {
		LOGGER.entering(CLASS_NAME, "updateStandingOrder", order);
		order.processed();
		BankMonitor.instance().updateStandingOrder(order);
		LOGGER.exiting(CLASS_NAME, "updateStandingOrder");
	}
}
