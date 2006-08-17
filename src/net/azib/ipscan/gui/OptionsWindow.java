/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.config.Labels;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.RowLayout;

/**
 * OptionsWindow
 *
 * @author anton
 */
public class OptionsWindow extends AbstractModalDialog {

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
	private Button skipBroadcastsCheckbox;
	private Composite fetchersTab;
	private Composite portsTab;
	private Text portTimeoutText;
	private Button adaptTimeoutCheckbox;
	private Text portsText;
	
	public OptionsWindow() {
		createShell();
		loadOptions();
	}
	
	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		shell = new Shell(currentDisplay != null ? currentDisplay.getActiveShell() : null, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		shell.setText(Labels.getInstance().getString("title.options"));
		createTabFolder();
		shell.setSize(new Point(350, 420));
		shell.setLayout(null);
		okButton = new Button(shell, SWT.NONE);
		okButton.setBounds(new Rectangle(175, 365, 75, 22));
		okButton.setText("OK");
		shell.setDefaultButton(okButton);
		cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setBounds(new Rectangle(260, 365, 75, 22));
		cancelButton.setText("Cancel");

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
		tabFolder.setBounds(new Rectangle(5, 5, 330, 355));
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Labels.getInstance().getString("title.options.scanning"));
		tabItem.setControl(scanningTab);
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText(Labels.getInstance().getString("title.options.ports"));
		tabItem1.setControl(portsTab);
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText(Labels.getInstance().getString("title.options.display"));
		tabItem2.setControl(displayTab);		
		TabItem tabItem3 = new TabItem(tabFolder, SWT.NONE);
		tabItem3.setText(Labels.getInstance().getString("title.options.fetchers"));
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
		threadsGroup.setText(Labels.getInstance().getString("options.threads"));
		threadsGroup.setLayout(groupLayout);

		GridData gridData = new GridData();
		gridData.widthHint = 50;
		
		Label label;
		
		label = new Label(threadsGroup, SWT.NONE);
		label.setText(Labels.getInstance().getString("options.threads.delay"));
		threadDelayText = new Text(threadsGroup, SWT.BORDER);
		threadDelayText.setLayoutData(gridData);

		label = new Label(threadsGroup, SWT.NONE);
		label.setText(Labels.getInstance().getString("options.threads.maxThreads"));
		maxThreadsText = new Text(threadsGroup, SWT.BORDER);
		maxThreadsText.setLayoutData(gridData);

		Group pingingGroup = new Group(scanningTab, SWT.NONE);
		pingingGroup.setLayout(groupLayout);
		pingingGroup.setText(Labels.getInstance().getString("options.pinging"));

		label = new Label(pingingGroup, SWT.NONE);
		label.setText(Labels.getInstance().getString("options.pinging.count"));
		pingingCountText = new Text(pingingGroup, SWT.BORDER);
		pingingCountText.setLayoutData(gridData);

		label = new Label(pingingGroup, SWT.NONE);
		label.setText(Labels.getInstance().getString("options.pinging.timeout"));
		pingingTimeoutText = new Text(pingingGroup, SWT.BORDER);
		pingingTimeoutText.setLayoutData(gridData);
		
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		deadHostsCheckbox = new Button(pingingGroup, SWT.CHECK);
		deadHostsCheckbox.setText(Labels.getInstance().getString("options.pinging.deadHosts"));
		deadHostsCheckbox.setLayoutData(gridData1);

		Group broadcastGroup = new Group(scanningTab, SWT.NONE);
		broadcastGroup.setLayout(groupLayout);
		broadcastGroup.setText(Labels.getInstance().getString("options.broadcast"));
		
		skipBroadcastsCheckbox = new Button(broadcastGroup, SWT.CHECK);
		skipBroadcastsCheckbox.setText(Labels.getInstance().getString("options.broadcast.skip"));
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
		timingGroup.setText(Labels.getInstance().getString("options.ports.timing"));
		timingGroup.setLayout(groupLayout);

		GridData gridData = new GridData();
		gridData.widthHint = 50;
		
		Label label;
		
		label = new Label(timingGroup, SWT.NONE);
		label.setText(Labels.getInstance().getString("options.ports.timing.timeout"));
		portTimeoutText = new Text(timingGroup, SWT.BORDER);
		portTimeoutText.setLayoutData(gridData);
		
		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 2;
		adaptTimeoutCheckbox = new Button(timingGroup, SWT.CHECK);
		adaptTimeoutCheckbox.setText(Labels.getInstance().getString("options.ports.timing.adaptTimeout"));
		adaptTimeoutCheckbox.setLayoutData(gridData1);
		
		RowLayout portsLayout = new RowLayout(SWT.VERTICAL);
		portsLayout.fill = true;		
		Group portsGroup = new Group(portsTab, SWT.NONE);
		portsGroup.setText(Labels.getInstance().getString("options.ports.ports"));
		portsGroup.setLayout(portsLayout);
		
		label = new Label(portsGroup, SWT.WRAP);
		label.setText(Labels.getInstance().getString("options.ports.portsDescription"));
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
		label.setText(Labels.getInstance().getString("options.fetchers.info"));
	}

	private void loadOptions() {
		GlobalConfig global = Config.getGlobal();
		maxThreadsText.setText(Integer.toString(global.maxThreads));
		threadDelayText.setText(Integer.toString(global.threadDelay));
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
