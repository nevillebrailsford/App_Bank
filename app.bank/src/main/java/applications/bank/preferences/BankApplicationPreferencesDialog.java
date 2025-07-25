package applications.bank.preferences;

import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import application.base.app.gui.ColorProvider;
import application.inifile.IniFile;
import application.preferences.PreferencesDialog;
import applications.bank.application.BankApplication;
import applications.bank.gui.BankGUIConstants;

public class BankApplicationPreferencesDialog extends PreferencesDialog {
	private static final long serialVersionUID = 1L;

	private JComboBox<String> backgroundColorChoice;
	private JLabel backgroundColorPreview;
	private Color backgroundColor;

	private JComboBox<String> chartLineColorChoice;
	private JLabel chartLineColorPreview;
	private Color chartLineColor;

	private JComboBox<String> trendLineColorChoice;
	private JLabel trendLineColorPreview;
	private Color trendLineColor;
	
	private JCheckBox hideInactive;

	/**
	 * Create the dialog.
	 * 
	 * @param parent - the owning frame.
	 */
	public BankApplicationPreferencesDialog(JFrame parent) {
		super(parent);
	}

	@Override
	public void additionalActionListeners() {
		backgroundColorChoice.addActionListener((event) -> {
			previewBackground();
		});
		chartLineColorChoice.addActionListener((event) -> {
			previewChartLine();
		});
		trendLineColorChoice.addActionListener((event) -> {
			previewTrendLine();
		});
	}

	@Override
	public void additionalGUIItems(JPanel contentPanel) {
		JLabel label1 = new JLabel("Chart Background Color:");
		backgroundColorChoice = new JComboBox<>();
		backgroundColorPreview = new JLabel("  ");
		backgroundColorPreview.setOpaque(true);
		contentPanel.add(label1);
		contentPanel.add(backgroundColorChoice);
		contentPanel.add(backgroundColorPreview);

		JLabel label2 = new JLabel("Chart Line Color:");
		chartLineColorChoice = new JComboBox<>();
		chartLineColorPreview = new JLabel("  ");
		chartLineColorPreview.setOpaque(true);
		contentPanel.add(label2);
		contentPanel.add(chartLineColorChoice);
		contentPanel.add(chartLineColorPreview);

		JLabel label3 = new JLabel("Trend Line Color:");
		trendLineColorChoice = new JComboBox<>();
		trendLineColorPreview = new JLabel("  ");
		trendLineColorPreview.setOpaque(true);
		contentPanel.add(label3);
		contentPanel.add(trendLineColorChoice);
		contentPanel.add(trendLineColorPreview);
		
		JLabel label4 = new JLabel("Hide Inactive Accounts:");
		hideInactive = new JCheckBox();
		hideInactive.setOpaque(false);
		contentPanel.add(label4);
		contentPanel.add(hideInactive);
		contentPanel.add(new JLabel(" "));

		initializeOptionalFields();
	}

	@Override
	public void saveAdditionalPreferences() {
		String background = (String) backgroundColorChoice.getSelectedItem();
		String chartLine = (String) chartLineColorChoice.getSelectedItem();
		String trendLine = (String) trendLineColorChoice.getSelectedItem();
		IniFile.store(BankGUIConstants.BACKGROUND_COLOR, background);
		IniFile.store(BankGUIConstants.CHART_LINE_COLOR, chartLine);
		IniFile.store(BankGUIConstants.TREND_LINE_COLOR, trendLine);
		IniFile.store(BankGUIConstants.HIDE_INACTIVE, Boolean.toString(hideInactive.isSelected()));
		if (background == null || background.isBlank() || background.equals("default")) {
			background = BankGUIConstants.DEFAULT_BACKGROUND_COLOR;
		}
		if (chartLine == null || chartLine.isEmpty() || chartLine.equals("default")) {
			chartLine = BankGUIConstants.DEFAULT_CHART_LINE_COLOR;
		}
		if (trendLine == null || trendLine.isEmpty() || trendLine.equals("default")) {
			trendLine = BankGUIConstants.DEFAULT_TREND_LINE_COLOR;
		}
		BankApplication.colorChoice = new ColorChoice(background, chartLine, trendLine);
	}

	private void initializeOptionalFields() {
		initializeColorChoice(backgroundColorChoice);
		if (!IniFile.value(BankGUIConstants.BACKGROUND_COLOR).isEmpty()) {
			backgroundColorChoice.setSelectedItem(IniFile.value(BankGUIConstants.BACKGROUND_COLOR));
		}
		initializeColorChoice(chartLineColorChoice);
		if (!IniFile.value(BankGUIConstants.CHART_LINE_COLOR).isEmpty()) {
			chartLineColorChoice.setSelectedItem(IniFile.value(BankGUIConstants.CHART_LINE_COLOR));
		}
		initializeColorChoice(trendLineColorChoice);
		if (!IniFile.value(BankGUIConstants.TREND_LINE_COLOR).isEmpty()) {
			trendLineColorChoice.setSelectedItem(IniFile.value(BankGUIConstants.TREND_LINE_COLOR));
		}
		previewBackground();
		previewChartLine();
		previewTrendLine();
		if (!IniFile.value(BankGUIConstants.HIDE_INACTIVE).isEmpty()) {
			hideInactive.setSelected(Boolean.valueOf(IniFile.value(BankGUIConstants.HIDE_INACTIVE)));
		} else {
			hideInactive.setSelected(true);
		}
	}

	private void initializeColorChoice(JComboBox<String> choice) {
		String[] colors = ColorProvider.name;
		choice.addItem("default");
		for (int i = 0; i < colors.length; i++) {
			choice.addItem(colors[i]);
		}
		choice.setSelectedIndex(0);
	}

	private Color getSelectedColor(JComboBox<String> choice, Color defaultColor) {
		Color result = null;
		if (choice.getSelectedItem().equals("default")) {
			result = defaultColor;
		} else {
			result = ColorProvider.get((String) choice.getSelectedItem());
		}
		return result;
	}

	private void previewBackground() {
		backgroundColor = getSelectedColor(backgroundColorChoice,
				ColorProvider.get(BankGUIConstants.DEFAULT_BACKGROUND_COLOR));
		backgroundColorPreview.setBackground(backgroundColor);
	}

	private void previewChartLine() {
		chartLineColor = getSelectedColor(chartLineColorChoice,
				ColorProvider.get(BankGUIConstants.DEFAULT_CHART_LINE_COLOR));
		chartLineColorPreview.setBackground(chartLineColor);
	}

	private void previewTrendLine() {
		trendLineColor = getSelectedColor(trendLineColorChoice,
				ColorProvider.get(BankGUIConstants.DEFAULT_TREND_LINE_COLOR));
		trendLineColorPreview.setBackground(trendLineColor);
	}

}
