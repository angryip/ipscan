/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.GlobalConfig.DisplayMethod;
import net.azib.ipscan.core.PortIterator;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.fetchers.FetcherException;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * Options Dialog
 *
 * @author Anton Keks
 */
public class OptionsDialog extends AbstractModalDialog {
	
	private PingerRegistry pingerRegistry;
	private GlobalConfig globalConfig;
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
	private Composite fetchersTab;
	private Composite portsTab;
	private TabItem portsTabItem;
	private Text portTimeoutText;
	private Button adaptTimeoutCheckbox;
	private Text portsText;
	private Text notAvailableText;
	private Text notScannedText;
	private Button[] displayMethod;
	private Button showInfoCheckbox;
	private Button askConfirmationCheckbox;
	
	public OptionsDialog(PingerRegistry pingerRegistry, GlobalConfig globalConfig, ConfigDetectorDialog configDetectorDialog) {
		this.pingerRegistry = pingerRegistry;
		this.globalConfig = globalConfig;
		this.configDetectorDialog = configDetectorDialog;
	}
	
	@Override
	public void open() {
		openTab(0);
	}
	
	/**
	 * Opens the specified tab of options dialog
	 * @param tabIndex
	 */
	public void openTab(int tabIndex) {
		// widgets are created on demand
		createShell();
		loadOptions();
		tabFolder.setSelection(tabIndex);
		
		// select ports text by default if ports tab is opened
		// this is needed for PortsFetcher that uses this tab as its options
		if (tabFolder.getItem(tabIndex) == portsTabItem) {
			portsText.forceFocus();
		}
		
		super.open();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();

		shell = new Shell(currentDisplay != null ? currentDisplay.getActiveShell() : null, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		shell.setText(Labels.getLabel("title.options"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));
		
		createTabFolder();

		okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));
		
		cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));
		
		positionButtonsInFormLayout(okButton, cancelButton, tabFolder);
		
		shell.pack();
		okButton.setFocus();

		okButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				saveOptions();
				shell.close();
			}
		});
		cancelButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
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
		tabItem.setText(Labels.getLabel("title.options.scanning"));
		tabItem.setControl(scanningTab);
		scanningTabItem = tabItem;
		
		createPortsTab();
		tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Labels.getLabel("title.options.ports"));
		tabItem.setControl(portsTab);
		portsTabItem = tabItem;
		
		createDisplayTab();		
		tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Labels.getLabel("title.options.display"));
		tabItem.setControl(displayTab);		

		createFetchersTab();
		tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Labels.getLabel("title.options.fetchers"));
		tabItem.setControl(fetchersTab);
		
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
		threadsGroup.setText(Labels.getLabel("options.threads"));
		threadsGroup.setLayout(groupLayout);

		GridData gridData = new GridData(80, SWT.DEFAULT);
		
		Label label;
		
		label = new Label(threadsGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.threads.delay"));
		threadDelayText = new Text(threadsGroup, SWT.BORDER);
		threadDelayText.setLayoutData(gridData);

		label = new Label(threadsGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.threads.maxThreads"));
		maxThreadsText = new Text(threadsGroup, SWT.BORDER);
		maxThreadsText.setLayoutData(gridData);
		new Label(threadsGroup, SWT.NONE);
		Button detectButton = new Button(threadsGroup, SWT.NONE);
		detectButton.setText(Labels.getLabel("button.check"));
		detectButton.setLayoutData(gridData);
		detectButton.addListener(SWT.Selection, new CheckButtonListener());

		Group pingingGroup = new Group(scanningTab, SWT.NONE);
		pingingGroup.setLayout(groupLayout);
		pingingGroup.setText(Labels.getLabel("options.pinging"));
		
		label = new Label(pingingGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.pinging.type"));
		pingersCombo = new Combo(pingingGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		pingersCombo.setLayoutData(gridData);
		String[] pingerNames = pingerRegistry.getRegisteredNames();
		for (int i = 0; i < pingerNames.length; i++) {
			pingersCombo.add(Labels.getLabel(pingerNames[i]));
			// this is used by saveOptions()
			pingersCombo.setData(Integer.toString(i), pingerNames[i]);
		}
		pingersCombo.select(0);

		label = new Label(pingingGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.pinging.count"));
		pingingCountText = new Text(pingingGroup, SWT.BORDER);
		pingingCountText.setLayoutData(gridData);

		label = new Label(pingingGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.pinging.timeout"));
		pingingTimeoutText = new Text(pingingGroup, SWT.BORDER);
		pingingTimeoutText.setLayoutData(gridData);
		
		GridData gridDataWithSpan = new GridData();
		gridDataWithSpan.horizontalSpan = 2;
		deadHostsCheckbox = new Button(pingingGroup, SWT.CHECK);
		deadHostsCheckbox.setText(Labels.getLabel("options.pinging.deadHosts"));
		deadHostsCheckbox.setLayoutData(gridDataWithSpan);

		Group broadcastGroup = new Group(scanningTab, SWT.NONE);
		broadcastGroup.setLayout(groupLayout);
		broadcastGroup.setText(Labels.getLabel("options.broadcast"));
		
		skipBroadcastsCheckbox = new Button(broadcastGroup, SWT.CHECK);
		skipBroadcastsCheckbox.setText(Labels.getLabel("options.broadcast.skip"));
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
		listGroup.setText(Labels.getLabel("options.display.list"));
		listGroup.setLayout(groupLayout);
		listGroup.setLayoutData(new RowData(260, SWT.DEFAULT));
		displayMethod = new Button[DisplayMethod.values().length];
		Button allRadio = new Button(listGroup, SWT.RADIO);
		allRadio.setText(Labels.getLabel("options.display.list" + '.' + DisplayMethod.ALL));
		displayMethod[DisplayMethod.ALL.ordinal()] = allRadio;
		Button aliveRadio = new Button(listGroup, SWT.RADIO);
		aliveRadio.setText(Labels.getLabel("options.display.list" + '.' + DisplayMethod.ALIVE));
		displayMethod[DisplayMethod.ALIVE.ordinal()] = aliveRadio;
		Button portsRadio = new Button(listGroup, SWT.RADIO);
		portsRadio.setText(Labels.getLabel("options.display.list" + '.' +  DisplayMethod.PORTS));
		displayMethod[DisplayMethod.PORTS.ordinal()] = portsRadio;
		
		groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		Group labelsGroup = new Group(displayTab, SWT.NONE);
		labelsGroup.setText(Labels.getLabel("options.display.labels"));
		labelsGroup.setLayout(groupLayout);
		
		GridData gridData = new GridData();
		gridData.widthHint = 50;
		
		Label label = new Label(labelsGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.display.labels.notAvailable"));
		notAvailableText = new Text(labelsGroup, SWT.BORDER);
		notAvailableText.setLayoutData(gridData);
		
		label = new Label(labelsGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.display.labels.notScanned"));
		notScannedText = new Text(labelsGroup, SWT.BORDER);
		notScannedText.setLayoutData(gridData);

		groupLayout = new GridLayout();
		groupLayout.numColumns = 1;
		Group showStatsGroup = new Group(displayTab, SWT.NONE);
		showStatsGroup.setLayout(groupLayout);
		showStatsGroup.setText(Labels.getLabel("options.display.confirmation"));
		
		askConfirmationCheckbox = new Button(showStatsGroup, SWT.CHECK);
		askConfirmationCheckbox.setText(Labels.getLabel("options.display.confirmation.newScan"));
		showInfoCheckbox = new Button(showStatsGroup, SWT.CHECK);
		showInfoCheckbox.setText(Labels.getLabel("options.display.confirmation.showInfo"));
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
		timingGroup.setText(Labels.getLabel("options.ports.timing"));
		timingGroup.setLayout(groupLayout);

		GridData gridData = new GridData();
		gridData.widthHint = 50;
		
		Label label;
		
		label = new Label(timingGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.ports.timing.timeout"));
		portTimeoutText = new Text(timingGroup, SWT.BORDER);
		portTimeoutText.setLayoutData(gridData);
		
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		adaptTimeoutCheckbox = new Button(timingGroup, SWT.CHECK);
		adaptTimeoutCheckbox.setText(Labels.getLabel("options.ports.timing.adaptTimeout"));
		adaptTimeoutCheckbox.setLayoutData(gridData1);
		
		RowLayout portsLayout = new RowLayout(SWT.VERTICAL);
		portsLayout.fill = true;
		portsLayout.marginHeight = 2;
		portsLayout.marginWidth = 2;
		Group portsGroup = new Group(portsTab, SWT.NONE);
		portsGroup.setText(Labels.getLabel("options.ports.ports"));
		portsGroup.setLayout(portsLayout);
		
		label = new Label(portsGroup, SWT.WRAP);
		label.setText(Labels.getLabel("options.ports.portsDescription"));
		//label.setLayoutData(new RowData(300, SWT.DEFAULT));
		portsText = new Text(portsGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		portsText.setLayoutData(new RowData(SWT.DEFAULT, 60));
		portsText.addKeyListener(new PortsTextValidationListener());
	}

	/**
	 * This method initializes fetchersTab	
	 */
	private void createFetchersTab() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		fetchersTab = new Composite(tabFolder, SWT.NONE);
		fetchersTab.setLayout(gridLayout);
		Label label = new Label(fetchersTab, SWT.NONE);
		label.setText(Labels.getLabel("options.fetchers.info"));
	}

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

	private void loadOptions() {
		maxThreadsText.setText(Integer.toString(globalConfig.maxThreads));
		threadDelayText.setText(Integer.toString(globalConfig.threadDelay));
		String[] pingerNames = pingerRegistry.getRegisteredNames();
		for (int i = 0; i < pingerNames.length; i++) {
			if (globalConfig.selectedPinger.equals(pingerNames[i])) {
				pingersCombo.select(i);
			}
		}
		pingingCountText.setText(Integer.toString(globalConfig.pingCount));
		pingingTimeoutText.setText(Integer.toString(globalConfig.pingTimeout));
		deadHostsCheckbox.setSelection(globalConfig.scanDeadHosts);
		skipBroadcastsCheckbox.setSelection(globalConfig.skipBroadcastAddresses);
		portTimeoutText.setText(Integer.toString(globalConfig.portTimeout));
		adaptTimeoutCheckbox.setSelection(globalConfig.adaptPortTimeout);
		portsText.setText(globalConfig.portString);
		notAvailableText.setText(globalConfig.notAvailableText);
		notScannedText.setText(globalConfig.notScannedText);
		displayMethod[globalConfig.displayMethod.ordinal()].setSelection(true);
		showInfoCheckbox.setSelection(globalConfig.showScanStats);
		askConfirmationCheckbox.setSelection(globalConfig.askScanConfirmation);
	}
	
	private void saveOptions() {
		// validate port string
		try {
			new PortIterator(portsText.getText());
		}
		catch (Exception e) {
			tabFolder.setSelection(portsTabItem);
			portsText.forceFocus();
			throw new FetcherException("unparseablePortString", e);
		}

		globalConfig.selectedPinger = (String) pingersCombo.getData(Integer.toString(pingersCombo.getSelectionIndex()));
		if (!pingerRegistry.checkSelectedPinger()) {
			tabFolder.setSelection(scanningTabItem);
			pingersCombo.forceFocus();
			throw new FetcherException("unsupportedPinger");
		}

		globalConfig.maxThreads = parseIntValue(maxThreadsText);
		globalConfig.threadDelay = parseIntValue(threadDelayText);
		globalConfig.pingCount = parseIntValue(pingingCountText);
		globalConfig.pingTimeout = parseIntValue(pingingTimeoutText);
		globalConfig.scanDeadHosts = deadHostsCheckbox.getSelection();
		globalConfig.skipBroadcastAddresses = skipBroadcastsCheckbox.getSelection();
		globalConfig.portTimeout = parseIntValue(portTimeoutText);
		globalConfig.adaptPortTimeout = adaptTimeoutCheckbox.getSelection();
		globalConfig.portString = portsText.getText();
		globalConfig.notAvailableText = notAvailableText.getText();
		globalConfig.notScannedText = notScannedText.getText();
		for (int i = 0; i < displayMethod.length; i++) {
			if (displayMethod[i].getSelection())
				globalConfig.displayMethod = DisplayMethod.values()[i];
		}
		globalConfig.showScanStats = showInfoCheckbox.getSelection();
		globalConfig.askScanConfirmation = askConfirmationCheckbox.getSelection();
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
			// current
			char c = e.character;
			if (Character.isISOControl(c) && !Character.isWhitespace(c))
				return;
			
			Text portsText = (Text) e.getSource();
			
			e.doit = validateChar(c, portsText.getText(), portsText.getCaretPosition());
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
			globalConfig.maxThreads = Integer.parseInt(maxThreadsText.getText());
			configDetectorDialog.open();
			loadOptions();
		}
	}
}
