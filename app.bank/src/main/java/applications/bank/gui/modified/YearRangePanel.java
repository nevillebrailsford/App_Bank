package applications.bank.gui.modified;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import application.base.app.gui.ColoredPanel;
import application.definition.ApplicationConfiguration;

public class YearRangePanel extends ColoredPanel {
	private static final long serialVersionUID = 1L;

	private static final String CLASS_NAME = YearRangePanel.class.getName();
	private static Logger LOGGER = ApplicationConfiguration.logger();

	private JLabel selectRangeLabel;
	private JComboBox<String> selectRange;
	
	public YearRangePanel(int fromYear, int toYear) {
		super(new GridLayout(1, 2, 10, 5));		
		LOGGER.entering(CLASS_NAME, "init");
		List<String> years = new ArrayList<>();
		for (int y = fromYear; y <= toYear; y++) {
			String year = String.valueOf(y) + "-" + String.valueOf(y + 1);
			years.add(year);
		}
		selectRangeLabel = new JLabel("Select tax year: ");
		selectRange = new JComboBox<>(years.toArray(new String[0]));
		add(selectRangeLabel);
		add(selectRange);
		LOGGER.exiting(CLASS_NAME, "init");
	}
	
	public String years() {
		LOGGER.entering(CLASS_NAME, "years");
		String years = (String) selectRange.getSelectedItem();
		LOGGER.exiting(CLASS_NAME, "years", years);
		return years;
	}
	
}
