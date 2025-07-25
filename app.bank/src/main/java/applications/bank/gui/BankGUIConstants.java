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

	public static final String DESCRIPTION_OPTIONS = "descriptionoptions";

	// indianred #cd5c5c
	public static final Color INDIAN_RED = new Color(205, 92, 92, 155);
	// lightgreen #90ee90
	public static final Color LIGHT_GREEN = new Color(144, 238, 144, 155);

	public static final String BACKGROUND_COLOR = "backgroundcolor";
	public static final String CHART_LINE_COLOR = "chartlinecolor";
	public static final String TREND_LINE_COLOR = "trendlinecolor";

	public static final String DEFAULT_BACKGROUND_COLOR = "floralwhite";
	public static final String DEFAULT_CHART_LINE_COLOR = "cornflowerblue";
	public static final String DEFAULT_TREND_LINE_COLOR = "palevioletred";
	
	public static final String HIDE_INACTIVE = "hideInactive";
}
