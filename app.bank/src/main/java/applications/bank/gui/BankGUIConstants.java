package applications.bank.gui;

import java.awt.Color;

import application.base.app.gui.GUIConstants;
import application.definition.BaseConstants;
import application.mail.MailConstants;
import application.storage.StorageConstants;
import applications.bank.storage.ModelConstants;

public class BankGUIConstants implements BaseConstants, StorageConstants, ModelConstants, MailConstants, GUIConstants {
	public static final String LAST_TIME = "lastTime";
	public static final String dateFormatForCalendarView = "EEE dd LLL uuuu";

	// indianred #cd5c5c
	public static final Color INDIAN_RED = new Color(205, 92, 92, 155);
	// lightgreen #90ee90
	public static final Color LIGHT_GREEN = new Color(144, 238, 144, 155);

}
