/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.net.PingerRegistry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * Options Dialog
 *
 * @author anton
 */
public class OptionsDialog extends AbstractModalDialog {
	
	private PingerRegistry pingerRegistry;

	private TabFolder tabFolder;
	private Composite scanningTab;
	private Composite displayTab;
	private Text threadDelayText;
	private Text maxThreadsText;
	private Button okButton;
	private Button cancelButton;
	private Button deadHostsCheckbox;
	private Text pingingTimeoutText;
	private Text pingingCountText;
	private Combo pingersCombo;
	private Button skipBroadcastsCheckbox;
	private Composite fetchersTab;
	private Composite portsTab;
	private Text portTimeoutText;
	private Button adaptTimeoutCheckbox;
	private Text portsText;
	
	public OptionsDialog(PingerRegistry pingerRegistry) {
		this.pingerRegistry = pingerRegistry;
	}
	
	public void open() {
		// widgets are created on demand
		createShell();
		loadOptions();
		super.open();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();

		shell = new Shell(currentDisplay != null ? currentDisplay.getActiveShell() : null, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		shell.setText(Labels.getLabel("title.options"));
		shell.setSize(new Point(380, 423));

		okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));
		
		cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));

		positionButtons(okButton, cancelButton);
		
		createTabFolder();
		Rectangle clientArea = shell.getClientArea();
		tabFolder.setBounds(new Rectangle(10, 10, clientArea.width - 20, okButton.getLocation().y - 20));

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
		createDisplayTab();
		createFetchersTab();
		createPortsTab();
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Labels.getLabel("title.options.scanning"));
		tabItem.setControl(scanningTab);
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText(Labels.getLabel("title.options.ports"));
		tabItem1.setControl(portsTab);
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText(Labels.getLabel("title.options.display"));
		tabItem2.setControl(displayTab);		
		TabItem tabItem3 = new TabItem(tabFolder, SWT.NONE);
		tabItem3.setText(Labels.getLabel("title.options.fetchers"));
		tabItem3.setControl(fetchersTab);
	}

	/**
	 * This method initializes scanningTab	
	 */
	private void createScanningTab() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = org.eclipse.swt.SWT.VERTICAL;
		rowLayout.marginTop = 9;
		rowLayout.spacing = 9;
		rowLayout.marginLeft = 11;
		rowLayout.fill = true;
		scanningTab = new Composite(tabFolder, SWT.NONE);
		scanningTab.setLayout(rowLayout);
		
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		Group threadsGroup = new Group(scanningTab, SWT.NONE);
		threadsGroup.setText(Labels.getLabel("options.threads"));
		threadsGroup.setLayout(groupLayout);

		GridData gridData = new GridData();
		gridData.widthHint = 80;
		
		Label label;
		
		label = new Label(threadsGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.threads.delay"));
		threadDelayText = new Text(threadsGroup, SWT.BORDER);
		threadDelayText.setLayoutData(gridData);

		label = new Label(threadsGroup, SWT.NONE);
		label.setText(Labels.getLabel("options.threads.maxThreads"));
		maxThreadsText = new Text(threadsGroup, SWT.BORDER);
		maxThreadsText.setLayoutData(gridData);

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
		
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		deadHostsCheckbox = new Button(pingingGroup, SWT.CHECK);
		deadHostsCheckbox.setText(Labels.getLabel("options.pinging.deadHosts"));
		deadHostsCheckbox.setLayoutData(gridData1);

		Group broadcastGroup = new Group(scanningTab, SWT.NONE);
		broadcastGroup.setLayout(groupLayout);
		broadcastGroup.setText(Labels.getLabel("options.broadcast"));
		
		skipBroadcastsCheckbox = new Button(broadcastGroup, SWT.CHECK);
		skipBroadcastsCheckbox.setText(Labels.getLabel("options.broadcast.skip"));
		skipBroadcastsCheckbox.setLayoutData(gridData1);
	}

	/**
	 * This method initializes displayTab	
	 */
	private void createDisplayTab() {
		displayTab = new Composite(tabFolder, SWT.NONE);
		displayTab.setLayout(new GridLayout());
	}
	
	/**
	 * This method initializes portsTab	
	 */
	private void createPortsTab() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = org.eclipse.swt.SWT.VERTICAL;
		rowLayout.marginTop = 9;
		rowLayout.spacing = 9;
		rowLayout.marginLeft = 11;
		rowLayout.fill = true;
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
		Group portsGroup = new Group(portsTab, SWT.NONE);
		portsGroup.setText(Labels.getLabel("options.ports.ports"));
		portsGroup.setLayout(portsLayout);
		
		label = new Label(portsGroup, SWT.WRAP);
		label.setText(Labels.getLabel("options.ports.portsDescription"));
		label.setLayoutData(new RowData(280, SWT.DEFAULT));
		portsText = new Text(portsGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		portsText.setLayoutData(new RowData(260, 60));
		// TODO: configuration string validation

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

	private void loadOptions() {
		GlobalConfig global = Config.getGlobal();
		maxThreadsText.setText(Integer.toString(global.maxThreads));
		threadDelayText.setText(Integer.toString(global.threadDelay));
		String[] pingerNames = pingerRegistry.getRegisteredNames();
		for (int i = 0; i < pingerNames.length; i++) {
			if (global.selectedPinger.equals(pingerNames[i])) {
				pingersCombo.select(i);
			}
		}
		pingingCountText.setText(Integer.toString(global.pingCount));
		pingingTimeoutText.setText(Integer.toString(global.pingTimeout));
		deadHostsCheckbox.setSelection(global.scanDeadHosts);
		skipBroadcastsCheckbox.setSelection(global.skipBroadcastAddresses);
		portTimeoutText.setText(Integer.toString(global.portTimeout));
		adaptTimeoutCheckbox.setSelection(global.adaptPortTimeout);
		portsText.setText(global.portString);
	}
	
	private void saveOptions() {
		GlobalConfig global = Config.getGlobal();
		global.maxThreads = parseIntValue(maxThreadsText);
		global.threadDelay = parseIntValue(threadDelayText);
		global.selectedPinger = (String) pingersCombo.getData(Integer.toString(pingersCombo.getSelectionIndex()));
		global.pingCount = parseIntValue(pingingCountText);
		global.pingTimeout = parseIntValue(pingingTimeoutText);
		global.scanDeadHosts = deadHostsCheckbox.getSelection();
		global.skipBroadcastAddresses = skipBroadcastsCheckbox.getSelection();
		global.portTimeout = parseIntValue(portTimeoutText);
		global.adaptPortTimeout = adaptTimeoutCheckbox.getSelection();
		global.portString = portsText.getText();
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
}
