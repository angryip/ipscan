/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FeederException;
import net.azib.ipscan.feeders.RangeFeeder;
import net.azib.ipscan.gui.actions.FeederActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;

import static net.azib.ipscan.config.Labels.getLabel;
import static net.azib.ipscan.util.InetAddressUtils.*;

/**
 * GUI for initialization of RangeFeeder.
 * 
 * @author Anton Keks
 */
@Singleton
public class RangeFeederGUI extends AbstractFeederGUI {
	private Text startIPText;
	private Text endIPText;
	private Text hostnameText;
	private Button ipUpButton;
	private Combo netmaskCombo;

	private boolean isEndIPUnedited = true;
	private boolean modifyListenersDisabled = false;

	@Inject public RangeFeederGUI(@Named("feederArea") Composite parent) {
		super(parent);
		feeder = new RangeFeeder();
	}

	public void initialize() {
		setLayout(new GridLayout(5, false));

		Label ipRangeLabel = new Label(this, SWT.NONE);
        startIPText = new Text(this, SWT.BORDER);
		Label toLabel = new Label(this, SWT.NONE);
        endIPText = new Text(this, SWT.BORDER);
		Label hostnameLabel = new Label(this, SWT.NONE);
        hostnameText = new Text(this, SWT.BORDER);
		ipUpButton = new Button(this, SWT.NONE);
        netmaskCombo = new Combo(this, SWT.NONE);

		// the longest possible IP
        startIPText.setText("255.255.255.255xx");
        int textWidth = startIPText.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        startIPText.setText("");
		startIPText.setLayoutData(new GridData(textWidth, -1));
		endIPText.setLayoutData(new GridData(textWidth, -1));
		hostnameText.setLayoutData(new GridData(textWidth, -1));
		netmaskCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		((GridData)endIPText.getLayoutData()).horizontalSpan = 2;
		GridData ipUpData = new GridData(); ipUpData.horizontalSpan = 2;
		ipUpButton.setLayoutData(ipUpData);

        ipRangeLabel.setText(getLabel("feeder.range") + ":");
		ipRangeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		hostnameLabel.setText(getLabel("feeder.range.hostname") + ":");
		hostnameLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        toLabel.setText(getLabel("feeder.range.to"));

		startIPText.addModifyListener(new StartIPModifyListener());
        endIPText.addKeyListener(new EndIPKeyListener());

		FeederActions.HostnameButton hostnameListener = new FeederActions.HostnameButton(hostnameText, startIPText, netmaskCombo) {
			public void widgetSelected(SelectionEvent event) {
				// raise the flag
				isEndIPUnedited = true;
				// reset the netmask combo
				netmaskCombo.setText(getLabel("feeder.range.netmask"));
				// now do the stuff
				super.widgetSelected(event);
			}
        };
        
        hostnameText.addTraverseListener(hostnameListener);
		hostnameText.setToolTipText(getLabel("feeder.range.hostname.tooltip"));
		
		Listener netmaskResetListener = new NetmaskResetListener();
		startIPText.addListener(SWT.Modify, netmaskResetListener);
		endIPText.addListener(SWT.Modify, netmaskResetListener);

		ipUpButton.setImage(new Image(getDisplay(), Labels.getInstance().getImageAsStream("button.ipUp.img")));
		ipUpButton.setText(getLabel("button.ipUp"));
		ipUpButton.addSelectionListener(hostnameListener);

        netmaskCombo.setText(getLabel("feeder.range.netmask"));
		netmaskCombo.setVisibleItemCount(10);
		netmaskCombo.add("/26");
		netmaskCombo.add("/24");
		netmaskCombo.add("/16");
		// Warning: IPv4 specific netmasks
		netmaskCombo.add("255...192");
		netmaskCombo.add("255...128");
		netmaskCombo.add("255...0");
		netmaskCombo.add("255..0.0");
		netmaskCombo.add("255.0.0.0");
		NetmaskListener netmaskSelectionListener = new NetmaskListener();
		netmaskCombo.addListener(SWT.Selection, netmaskSelectionListener);
		netmaskCombo.addListener(SWT.Traverse, netmaskSelectionListener);
		netmaskCombo.setToolTipText(getLabel("feeder.range.netmask.tooltip"));

		pack();

		// do this stuff asynchronously (to show GUI faster)
		asyncFillLocalHostInfo(hostnameText, startIPText);
	}

	public Feeder createFeeder() {
		feeder = new RangeFeeder(startIPText.getText(), endIPText.getText());
		return feeder;
	}
	
	public String[] serialize() {
		return new String[] {startIPText.getText(), endIPText.getText()};
	}

	public void unserialize(String[] parts) {
		// TODO: netmask support from the command-line
		startIPText.setText(parts[0]);
		endIPText.setText(parts[1]);
		// reset the netmask combo
		netmaskCombo.setText(getLabel("feeder.range.netmask"));
	}

	public String[] serializePartsLabels() {
		return new String[] {"feeder.range.startIP", "feeder.range.endIP"};
	}

	final class EndIPKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			isEndIPUnedited = false;
		}

		public void keyReleased(KeyEvent e) {
		}
	}
	
	final class NetmaskResetListener implements Listener {
		public void handleEvent(Event event) {
			// reset the netmask combo
			if (!modifyListenersDisabled)
				netmaskCombo.setText(getLabel("feeder.range.netmask"));
		}
	}

	final class StartIPModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			if (isEndIPUnedited) {
				endIPText.setText(startIPText.getText());
			}
		}
	}

	final class NetmaskListener implements Listener {
		public void handleEvent(Event event) {
			if (event.type == SWT.Traverse) {
				// skip any other traversal besides RETURN
				if (event.detail != SWT.TRAVERSE_RETURN) 
					return;
				event.doit = false;
			}
			if (event.type == SWT.Selection) {
				// workaround for GTK: this event is fired after keypresses, but we want it to be fired
				// only if something is selected from the drop down
				if (netmaskCombo.indexOf(netmaskCombo.getText()) < 0) return;

				// workaround for Windows: selection event is fired when the dropdown is opened
				if (Platform.WINDOWS && netmaskCombo.getListVisible()) return;
			}
			
			try {
				String netmaskString = netmaskCombo.getText();
				InetAddress startIP = InetAddress.getByName(startIPText.getText());
				updateStartEndWithNetmask(startIP, netmaskString);
			}
			catch (UnknownHostException e) {
				throw new FeederException("invalidNetmask");
			}
			
			if (event.type == SWT.Traverse) {
				// try to focus the start button
				getParent().forceFocus();
			} 
			else {
				netmaskCombo.forceFocus();
			}
		}
	}

	private void updateStartEndWithNetmask(InetAddress ip, String netmaskString) {
		try {
			InetAddress netmask = parseNetmask(netmaskString);
			modifyListenersDisabled = true;
			startIPText.setText(startRangeByNetmask(ip, netmask).getHostAddress());
			endIPText.setText(endRangeByNetmask(ip, netmask).getHostAddress());
			modifyListenersDisabled = false;
			isEndIPUnedited = false;
		}
		catch (UnknownHostException e) {
			LOG.fine(e.toString());
		}
	}

	@Override
	protected void afterLocalHostInfoFilled(InterfaceAddress localInterface) {
		InetAddress address = localInterface.getAddress();
		if (!address.isLoopbackAddress())
			updateStartEndWithNetmask(address, "/" + localInterface.getNetworkPrefixLength());
	}
}
