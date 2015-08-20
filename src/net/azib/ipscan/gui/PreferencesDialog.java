/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.GUIConfig.DisplayMethod;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.PortIterator;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.fetchers.FetcherException;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

/**
 * Preferences Dialog
 *
 * @author Anton Keks
 */
public class PreferencesDialog extends AbstractModalDialog {
	
	private PingerRegistry pingerRegistry;
	private Config globalConfig;
	private ScannerConfig scannerConfig;
	private GUIConfig guiConfig;
	private ConfigDetectorDialog configDetectorDialog;
	
	private Button okButton;
	private Button cancelButton;

	private TabFolder tabFolder;
	private Composite scanningTab;
	private TabItem scanningTabItem;
	private Composite displayTab;
	private Text threadDelayText;
	private Text maxThreadsText;
	private Button deadHostsCheckbox;
	private Text pingingTimeoutText;
	private Text pingingCountText;
	private Combo pingersCombo;
	private Button skipBroadcastsCheckbox;
//	private Composite fetchersTab;
	private Composite portsTab;
	private TabItem portsTabItem;
	private Text portTimeoutText;
	private Button adaptTimeoutCheckbox;
	private Button addRequestedPortsCheckbox;
	private Text minPortTimeoutText;
	private Text portsText;
	private Text notAvailableText;
	private Text notScannedText;
	private Button[] displayMethod;
	private Button showInfoCheckbox;
	private Button askConfirmationCheckbox;
	private Combo languageCombo;
	private String[] languages = { "system", "en", "hu", "lt", "es", "ku", "tr" };
	
	public PreferencesDialog(PingerRegistry pingerRegistry, Config globalConfig, ScannerConfig scannerConfig, GUIConfig guiConfig, ConfigDetectorDialog configDetectorDialog) {
		this.pingerRegistry = pingerRegistry;
		this.globalConfig = globalConfig;
		this.scannerConfig = scannerConfig;
		this.guiConfig = guiConfig;
		this.configDetectorDialog = configDetectorDialog;
	}
	
	@Override
	public void open() {
		openTab(0);
	}
	
	/**
	 * Opens the specified tab of preferences dialog
	 * @param tabIndex
	 */
	public void openTab(int tabIndex) {
		// widgets are created on demand
		createShell();
		loadPreferences();
		tabFolder.setSelection(tabIndex);
		
		// select ports text by default if ports tab is opened
		// this is needed for PortsFetcher that uses this tab as its preferences
		if (tabFolder.getItem(tabIndex) == portsTabItem) {
			portsText.forceFocus();
		}
		
		super.open();
	}

	@Override
	protected void populateShell() {
		shell.setText(Labels.getLabel("title.preferences"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));
		
		createTabFolder();

		okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));
		
		cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));
		
		positionButtonsInFormLayout(okButton, cancelButton, tabFolder);
		
		shell.pack();
		okButton.setFocus();

		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				savePreferences();
				shell.close();
			}
		});
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}

	/**
	 * This method initializes tabFolder	
	 */
	private void createTabFolder() {
		tabFolder = new TabFolder(shell, SWT.NONE);
		
		createScanningTab();
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Labels.getLabel("title.preferences.scanning"));
		tabItem.setControl(scanningTab);
		scanningTabItem = tabItem;
		
		createPortsTab();
		tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Labels.getLabel("title.preferences.ports"));
		tabItem.setControl(portsTab);
		portsTabItem = tabItem;
		
		createDisplayTab();		
		tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Labels.getLabel("title.preferences.display"));
		tabItem.setControl(displayTab);		

//		createFetchersTab();
//		tabItem = new TabItem(tabFolder, SWT.NONE);
//		tabItem.setText(Labels.getLabel("title.preferences.fetchers"));
//		tabItem.setControl(fetchersTab);
		
		tabFolder.pack();
	}

	/**
	 * This method initializes scanningTab	
	 */
	private void createScanningTab() {
		RowLayout rowLayout = createRowLayout();
		scanningTab = new Composite(tabFolder, SWT.NONE);
		scanningTab.setLayout(rowLayout);
		
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		Group threadsGroup = new Group(scanningTab, SWT.NONE);
		threadsGroup.setText(Labels.getLabel("preferences.threads"));
		threadsGroup.setLayout(groupLayout);

		GridData gridData = new GridData(80, SWT.DEFAULT);
		
		Label label;
		
		label = new Label(threadsGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.threads.delay"));
		threadDelayText = new Text(threadsGroup, SWT.BORDER);
		threadDelayText.setLayoutData(gridData);

		label = new Label(threadsGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.threads.maxThreads"));
		maxThreadsText = new Text(threadsGroup, SWT.BORDER);
		maxThreadsText.setLayoutData(gridData);
//		new Label(threadsGroup, SWT.NONE);
//		Button checkButton = new Button(threadsGroup, SWT.NONE);
//		checkButton.setText(Labels.getLabel("button.check"));
//		checkButton.setLayoutData(gridData);
//		checkButton.addListener(SWT.Selection, new CheckButtonListener());

		Group pingingGroup = new Group(scanningTab, SWT.NONE);
		pingingGroup.setLayout(groupLayout);
		pingingGroup.setText(Labels.getLabel("preferences.pinging"));
		
		label = new Label(pingingGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.pinging.type"));
		pingersCombo = new Combo(pingingGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		pingersCombo.setLayoutData(gridData);
		String[] pingerNames = pingerRegistry.getRegisteredNames();
		for (int i = 0; i < pingerNames.length; i++) {
			pingersCombo.add(Labels.getLabel(pingerNames[i]));
			// this is used by savePreferences()
			pingersCombo.setData(Integer.toString(i), pingerNames[i]);
		}
		pingersCombo.select(0);

		label = new Label(pingingGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.pinging.count"));
		pingingCountText = new Text(pingingGroup, SWT.BORDER);
		pingingCountText.setLayoutData(gridData);

		label = new Label(pingingGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.pinging.timeout"));
		pingingTimeoutText = new Text(pingingGroup, SWT.BORDER);
		pingingTimeoutText.setLayoutData(gridData);
		
		GridData gridDataWithSpan = new GridData();
		gridDataWithSpan.horizontalSpan = 2;
		deadHostsCheckbox = new Button(pingingGroup, SWT.CHECK);
		deadHostsCheckbox.setText(Labels.getLabel("preferences.pinging.deadHosts"));
		deadHostsCheckbox.setLayoutData(gridDataWithSpan);

		Group skippingGroup = new Group(scanningTab, SWT.NONE);
		skippingGroup.setLayout(groupLayout);
		skippingGroup.setText(Labels.getLabel("preferences.skipping"));
		
		skipBroadcastsCheckbox = new Button(skippingGroup, SWT.CHECK);
		skipBroadcastsCheckbox.setText(Labels.getLabel("preferences.skipping.broadcast"));
		GridData gridDataWithSpan2 = new GridData();
		gridDataWithSpan2.horizontalSpan = 2;
		skipBroadcastsCheckbox.setLayoutData(gridDataWithSpan2);
	}

	/**
	 * This method initializes displayTab	
	 */
	private void createDisplayTab() {
		RowLayout rowLayout = createRowLayout();
		displayTab = new Composite(tabFolder, SWT.NONE);
		displayTab.setLayout(rowLayout);
		
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 1;
		Group listGroup = new Group(displayTab, SWT.NONE);
		listGroup.setText(Labels.getLabel("preferences.display.list"));
		listGroup.setLayout(groupLayout);
		listGroup.setLayoutData(new RowData(260, SWT.DEFAULT));
		displayMethod = new Button[DisplayMethod.values().length];
		Button allRadio = new Button(listGroup, SWT.RADIO);
		allRadio.setText(Labels.getLabel("preferences.display.list" + '.' + DisplayMethod.ALL));
		displayMethod[DisplayMethod.ALL.ordinal()] = allRadio;
		Button aliveRadio = new Button(listGroup, SWT.RADIO);
		aliveRadio.setText(Labels.getLabel("preferences.display.list" + '.' + DisplayMethod.ALIVE));
		displayMethod[DisplayMethod.ALIVE.ordinal()] = aliveRadio;
		Button portsRadio = new Button(listGroup, SWT.RADIO);
		portsRadio.setText(Labels.getLabel("preferences.display.list" + '.' +  DisplayMethod.PORTS));
		displayMethod[DisplayMethod.PORTS.ordinal()] = portsRadio;
		
		groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		Group labelsGroup = new Group(displayTab, SWT.NONE);
		labelsGroup.setText(Labels.getLabel("preferences.display.labels"));
		labelsGroup.setLayout(groupLayout);
		
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		
		Label label = new Label(labelsGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.display.labels.notAvailable"));
		notAvailableText = new Text(labelsGroup, SWT.BORDER);
		notAvailableText.setLayoutData(gridData);
		
		label = new Label(labelsGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.display.labels.notScanned"));
		notScannedText = new Text(labelsGroup, SWT.BORDER);
		notScannedText.setLayoutData(gridData);

		groupLayout = new GridLayout();
		groupLayout.numColumns = 1;
		Group showStatsGroup = new Group(displayTab, SWT.NONE);
		showStatsGroup.setLayout(groupLayout);
		showStatsGroup.setText(Labels.getLabel("preferences.display.confirmation"));
		
		askConfirmationCheckbox = new Button(showStatsGroup, SWT.CHECK);
		askConfirmationCheckbox.setText(Labels.getLabel("preferences.display.confirmation.newScan"));
		showInfoCheckbox = new Button(showStatsGroup, SWT.CHECK);
		showInfoCheckbox.setText(Labels.getLabel("preferences.display.confirmation.showInfo"));
		
		groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		
		Group languageGroup = new Group(displayTab, SWT.NONE);
		languageGroup.setLayout(groupLayout);
		languageGroup.setText(Labels.getLabel("preferences.language"));
		
		languageCombo = new Combo(languageGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (String language : languages) {
			languageCombo.add(Labels.getLabel("language." + language));
		}
		languageCombo.select(0);

		label = new Label(languageGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.language.someIncomplete"));
	}
	
	/**
	 * This method initializes portsTab	
	 */
	private void createPortsTab() {
		RowLayout rowLayout = createRowLayout();
		portsTab = new Composite(tabFolder, SWT.NONE);
		portsTab.setLayout(rowLayout);
		
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		Group timingGroup = new Group(portsTab, SWT.NONE);
		timingGroup.setText(Labels.getLabel("preferences.ports.timing"));
		timingGroup.setLayout(groupLayout);

		GridData gridData = new GridData();
		gridData.widthHint = 50;
		
		Label label = new Label(timingGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.ports.timing.timeout"));
		portTimeoutText = new Text(timingGroup, SWT.BORDER);
		portTimeoutText.setLayoutData(gridData);
		
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		adaptTimeoutCheckbox = new Button(timingGroup, SWT.CHECK);
		adaptTimeoutCheckbox.setText(Labels.getLabel("preferences.ports.timing.adaptTimeout"));
		adaptTimeoutCheckbox.setLayoutData(gridData1);
		adaptTimeoutCheckbox.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				minPortTimeoutText.setEnabled(adaptTimeoutCheckbox.getSelection());
			}
		});

		label = new Label(timingGroup, SWT.NONE);
		label.setText(Labels.getLabel("preferences.ports.timing.minTimeout"));
		minPortTimeoutText = new Text(timingGroup, SWT.BORDER);
		minPortTimeoutText.setLayoutData(gridData);

		RowLayout portsLayout = new RowLayout(SWT.VERTICAL);
		portsLayout.fill = true;
		portsLayout.marginHeight = 2;
		portsLayout.marginWidth = 2;
		Group portsGroup = new Group(portsTab, SWT.NONE);
		portsGroup.setText(Labels.getLabel("preferences.ports.ports"));
		portsGroup.setLayout(portsLayout);
		
		label = new Label(portsGroup, SWT.WRAP);
		label.setText(Labels.getLabel("preferences.ports.portsDescription"));
		//label.setLayoutData(new RowData(300, SWT.DEFAULT));
		portsText = new Text(portsGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		portsText.setLayoutData(new RowData(SWT.DEFAULT, 60));
		portsText.addKeyListener(new PortsTextValidationListener());
		
		addRequestedPortsCheckbox = new Button(portsGroup, SWT.CHECK);
		addRequestedPortsCheckbox.setText(Labels.getLabel("preferences.ports.addRequested"));
		addRequestedPortsCheckbox.setToolTipText(Labels.getLabel("preferences.ports.addRequested.info"));
	}

	/**
	 * This method initializes fetchersTab	
	 */
//	private void createFetchersTab() {
//		GridLayout gridLayout = new GridLayout();
//		gridLayout.numColumns = 1;
//		fetchersTab = new Composite(tabFolder, SWT.NONE);
//		fetchersTab.setLayout(gridLayout);
//		Label label = new Label(fetchersTab, SWT.NONE);
//		label.setText(Labels.getLabel("preferences.fetchers.info"));
//	}

	/**
	 * @return a pre-initialized RowLayout suitable for option tabs.
	 */
	private RowLayout createRowLayout() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = org.eclipse.swt.SWT.VERTICAL;
		rowLayout.spacing = 9;
		rowLayout.marginHeight = 9;
		rowLayout.marginWidth = 11;
		rowLayout.fill = true;
		return rowLayout;
	}

	private void loadPreferences() {
    pingerRegistry.checkSelectedPinger();
		maxThreadsText.setText(Integer.toString(scannerConfig.maxThreads));
		threadDelayText.setText(Integer.toString(scannerConfig.threadDelay));
		String[] pingerNames = pingerRegistry.getRegisteredNames();
		for (int i = 0; i < pingerNames.length; i++) {
			if (scannerConfig.selectedPinger.equals(pingerNames[i])) {
				pingersCombo.select(i);
			}
		}
		pingingCountText.setText(Integer.toString(scannerConfig.pingCount));
		pingingTimeoutText.setText(Integer.toString(scannerConfig.pingTimeout));
		deadHostsCheckbox.setSelection(scannerConfig.scanDeadHosts);
		skipBroadcastsCheckbox.setSelection(scannerConfig.skipBroadcastAddresses);
		portTimeoutText.setText(Integer.toString(scannerConfig.portTimeout));
		adaptTimeoutCheckbox.setSelection(scannerConfig.adaptPortTimeout);
		minPortTimeoutText.setText(Integer.toString(scannerConfig.minPortTimeout));
		minPortTimeoutText.setEnabled(scannerConfig.adaptPortTimeout);
		portsText.setText(scannerConfig.portString);
		addRequestedPortsCheckbox.setSelection(scannerConfig.useRequestedPorts);
		notAvailableText.setText(scannerConfig.notAvailableText);
		notScannedText.setText(scannerConfig.notScannedText);
		displayMethod[guiConfig.displayMethod.ordinal()].setSelection(true);
		showInfoCheckbox.setSelection(guiConfig.showScanStats);
		askConfirmationCheckbox.setSelection(guiConfig.askScanConfirmation);
		for (int i = 0; i < languages.length; i++) {
			if (globalConfig.language.equals(languages[i])) {
				languageCombo.select(i);
			}
		}
	}
	
	private void savePreferences() {
		// validate port string
		try {
			new PortIterator(portsText.getText());
		}
		catch (Exception e) {
			tabFolder.setSelection(portsTabItem);
			portsText.forceFocus();
			throw new FetcherException("unparseablePortString", e);
		}

		scannerConfig.selectedPinger = (String) pingersCombo.getData(Integer.toString(pingersCombo.getSelectionIndex()));
		if (!pingerRegistry.checkSelectedPinger()) {
			tabFolder.setSelection(scanningTabItem);
			pingersCombo.forceFocus();
			throw new FetcherException("unsupportedPinger");
		}

		scannerConfig.maxThreads = parseIntValue(maxThreadsText);
		scannerConfig.threadDelay = parseIntValue(threadDelayText);
		scannerConfig.pingCount = parseIntValue(pingingCountText);
		scannerConfig.pingTimeout = parseIntValue(pingingTimeoutText);
		scannerConfig.scanDeadHosts = deadHostsCheckbox.getSelection();
		scannerConfig.skipBroadcastAddresses = skipBroadcastsCheckbox.getSelection();
		scannerConfig.portTimeout = parseIntValue(portTimeoutText);
		scannerConfig.adaptPortTimeout = adaptTimeoutCheckbox.getSelection();
		scannerConfig.minPortTimeout = parseIntValue(minPortTimeoutText);
		scannerConfig.portString = portsText.getText();
		scannerConfig.useRequestedPorts = addRequestedPortsCheckbox.getSelection();
		scannerConfig.notAvailableText = notAvailableText.getText();
		scannerConfig.notScannedText = notScannedText.getText();
		for (int i = 0; i < displayMethod.length; i++) {
			if (displayMethod[i].getSelection())
				guiConfig.displayMethod = DisplayMethod.values()[i];
		}
		guiConfig.showScanStats = showInfoCheckbox.getSelection();
		guiConfig.askScanConfirmation = askConfirmationCheckbox.getSelection();
		String newLanguage = languages[languageCombo.getSelectionIndex()];
		if (!newLanguage.equals(globalConfig.language)) {
			globalConfig.language = newLanguage;
			MessageBox msgBox = new MessageBox(shell);
			msgBox.setMessage(Labels.getLabel("preferences.language.needsRestart"));
			msgBox.open();
		}
	}

	/**
	 * @return an int from the passed Text control.
	 */
	private static int parseIntValue(Text text) {
		try {
			return Integer.parseInt(text.getText());
		}
		catch (NumberFormatException e) {
			text.forceFocus();
			throw e;
		}
	}
	
	static class PortsTextValidationListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			Text portsText = (Text) e.getSource();
			
			if (e.keyCode == SWT.TAB) {
				portsText.getShell().traverse(SWT.TRAVERSE_TAB_NEXT);
				e.doit = false;
				return;
			}
			else 
			if (e.keyCode == SWT.CR) {
				if ((e.stateMask & SWT.MOD1) > 0) {
					// allow ctrl+enter to insert newlines
					e.stateMask = 0; 
				}
				else {
					// single-enter will traverse
					portsText.getShell().traverse(SWT.TRAVERSE_RETURN);
					e.doit = false;
					return;
				}
			}
			else 
			if (Character.isISOControl(e.character)) {
				return;
			}
			
			e.doit = validateChar(e.character, portsText.getText(), portsText.getCaretPosition());
		}
		
		boolean validateChar(char c, String text, int caretPos) {
			// previous
			char pc = 0;
			for (int i = caretPos-1; i >= 0; i--) {
				pc = text.charAt(i);
				if (!Character.isWhitespace(pc))
					break;
			}
			
			boolean isCurDigit = c >= '0' && c <= '9';
			boolean isPrevDigit = pc >= '0' && pc <= '9';
			return isPrevDigit && (isCurDigit || c == '-' || c == ',') ||
				   isCurDigit && (pc == '-' || pc == ',' || pc == 0) ||
				   Character.isWhitespace(c) && pc == ',';
			
		}

		public void keyReleased(KeyEvent e) {
		}
	}
	
	class CheckButtonListener implements Listener {
		public void handleEvent(Event event) {
			scannerConfig.maxThreads = Integer.parseInt(maxThreadsText.getText());
			configDetectorDialog.open();
		}
	}
}
