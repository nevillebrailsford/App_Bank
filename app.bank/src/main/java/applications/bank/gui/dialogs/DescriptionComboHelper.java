package applications.bank.gui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComboBox;

import application.inifile.IniFile;
import applications.bank.gui.BankGUIConstants;

/**
 * Helper class to set up the values in a JComboBox that is used for
 * descriptions when transferring money, or making a payment.
 */
public class DescriptionComboHelper {

	/**
	 * Save the options offered in the JComboBox to the ini file.
	 * 
	 * @param description - the JComboBox
	 */
	public static void saveDescriptionOptions(JComboBox<String> description) {
		String options = "";
		String delim = "";
		if (description.getSelectedItem() == null) {
			return;
		}
		for (int i = 0; i < description.getItemCount(); i++) {
			if (description.getItemAt(i).equals(description.getSelectedItem())) {
				return;
			}
		}
		for (int i = 0; i < description.getItemCount(); i++) {
			options += delim;
			options += description.getItemAt(i);
			delim = ",";
		}
		options += delim;
		options += description.getSelectedItem();
		IniFile.store(BankGUIConstants.DESCRIPTION_OPTIONS, options);
	}

	/**
	 * Load the options to be offered in the JComboBox from the ini file.
	 * 
	 * @param description - the JComboBox
	 */
	public static void loadDescriptionOptions(JComboBox<String> description) {
		List<String> options = getDescriptionOptions();
		for (String option : options) {
			description.addItem(option);
		}
		description.setSelectedIndex(-1);
	}

	private static List<String> getDescriptionOptions() {
		List<String> options = new ArrayList<>();
		String iniOption = IniFile.value(BankGUIConstants.DESCRIPTION_OPTIONS);
		if (!iniOption.isEmpty()) {
			StringTokenizer tok = new StringTokenizer(iniOption, ",");
			while (tok.hasMoreTokens()) {
				options.add(tok.nextToken());
			}
		}
		Collections.sort(options);
		return options;
	}

}
