package applications.bank.gui.modified;

import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.toedter.calendar.JDateChooser;

import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;

public class DateRangePanel extends ColoredPanel {
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = DateRangePanel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private JDateChooser fromDate;
	private JDateChooser toDate;
	private JLabel fromDateLabel;
	private JLabel toDateLabel;
	
	public DateRangePanel() {
		super(new GridLayout(2, 2, 10, 5));		
		LOGGER.entering(CLASS_NAME, "init");
		fromDateLabel = new JLabel("From Date: ");
		fromDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		fromDate = new JDateChooser();
		toDateLabel = new JLabel("To Date: ");
		toDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		toDate = new JDateChooser();
		add(fromDateLabel);
		add(fromDate);
		add(toDateLabel);
		add(toDate);
		LOGGER.exiting(CLASS_NAME, "init");
	}
	
	public LocalDate fromDate() {
		LocalDate date;
		LOGGER.entering(CLASS_NAME, "fromDate");
		if (fromDate.getDate() == null) {
			date = LocalDate.of(2000, 1, 1);
		} else {
			date = fromDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		LOGGER.exiting(CLASS_NAME, "fromDate", date);
		return date;
	}

	public LocalDate toDate() {
		LocalDate date;
		LOGGER.entering(CLASS_NAME, "toDate");
		if (toDate.getDate() == null) {
			date = LocalDate.now().plusDays(1);
		} else {
			date = toDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		LOGGER.exiting(CLASS_NAME, "fromDate", date);
		return date;
	}

}
