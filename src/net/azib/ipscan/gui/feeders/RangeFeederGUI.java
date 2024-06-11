/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.Platform;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FeederException;
import net.azib.ipscan.feeders.RangeFeeder;
import net.azib.ipscan.gui.actions.FeederActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

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
public class RangeFeederGUI extends AbstractFeederGUI {
	private Text startIPText;
	private Text endIPText;
	private Text hostnameText;
	private Combo netmaskCombo;

	private boolean isEndIPUnedited = true;
	private boolean modifyListenersDisabled = false;

	public RangeFeederGUI(FeederArea parent) {
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
		Button ipUpButton = new Button(this, SWT.NONE);
        netmaskCombo = new Combo(this, SWT.NONE);

		// the longest possible IP
        startIPText.setText("255.255.255.255xx");
        int textWidth = startIPText.computeSize(-1, -1).x;
        startIPText.setText("");
		startIPText.setLayoutData(new GridData(textWidth, -1));
		endIPText.setLayoutData(new GridData(textWidth, -1));
		hostnameText.setLayoutData(new GridData(textWidth, -1));
		netmaskCombo.setLayoutData(new GridData(textWidth, -1));

		((GridData)endIPText.getLayoutData()).horizontalSpan = 2;
		GridData ipUpData = new GridData(); ipUpData.horizontalSpan = 2;
		ipUpButton.setLayoutData(ipUpData);

        ipRangeLabel.setText(getLabel("feeder.range") + ":");
		ipRangeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		hostnameLabel.setText(getLabel("feeder.range.hostname") + ":");
		hostnameLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        toLabel.setText(getLabel("feeder.range.to"));

		startIPText.addModifyListener(new StartIPModifyListener());
        endIPText.addModifyListener(new EndIPModifyListener());

		FeederActions.HostnameButton hostnameListener = new FeederActions.HostnameButton(hostnameText, startIPText, netmaskCombo) {
			public void widgetSelected(SelectionEvent event) {
				isEndIPUnedited = true;
				netmaskCombo.setText(getLabel("feeder.range.netmask"));
				super.widgetSelected(event);
			}

			protected void setInterfaceAddress(InterfaceAddress ifAddr) {
				afterLocalHostInfoFilled(ifAddr);
			}
        };
        
        hostnameText.addTraverseListener(hostnameListener);
		hostnameText.setToolTipText(getLabel("feeder.range.hostname.tooltip"));
		
		Listener netmaskResetListener = new NetmaskResetListener();
		startIPText.addListener(SWT.Modify, netmaskResetListener);
		endIPText.addListener(SWT.Modify, netmaskResetListener);

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
		Rectangle comboBounds = netmaskCombo.getBounds();
		Rectangle endIPBounds = endIPText.getBounds();
		int width = endIPBounds.x + endIPBounds.width - comboBounds.x - 5;
		if (Platform.WINDOWS) width -= 22; // TODO: remove width of down arrow, this number may change with updated SWT version
		if (Platform.MAC_OS) width += 10;
		((GridData) netmaskCombo.getLayoutData()).widthHint = width;
		pack();

		// do this stuff asynchronously (to show GUI faster)
		asyncFillLocalHostInfo(hostnameText, startIPText);
	}

	public Feeder createFeeder() {
		return feeder = new RangeFeeder(startIPText.getText().trim(), endIPText.getText().trim());
	}
	
	public String[] serialize() {
		return new String[] {startIPText.getText().trim(), endIPText.getText().trim()};
	}

	public void unserialize(String[] parts) {
		// TODO: netmask support from the command-line
		startIPText.setText(parts[0]);
		endIPText.setText(parts[1]);
		netmaskCombo.setText(getLabel("feeder.range.netmask"));
	}

	public String[] serializePartsLabels() {
		return new String[] {"feeder.range.startIP", "feeder.range.endIP"};
	}

	final class EndIPModifyListener implements ModifyListener {
		@Override public void modifyText(ModifyEvent modifyEvent) {
			isEndIPUnedited = false;
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
			if (isEndIPUnedited) endIPText.setText(startIPText.getText());
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

	@Override protected void afterLocalHostInfoFilled(InterfaceAddress ifAddr) {
		InetAddress address = ifAddr.getAddress();
		if (!address.isLoopbackAddress()) {
			updateStartEndWithNetmask(address, "/" + ifAddr.getNetworkPrefixLength());
			isEndIPUnedited = true;
		}
	}
}
