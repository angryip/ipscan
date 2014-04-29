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
import net.azib.ipscan.util.InetAddressUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static net.azib.ipscan.config.Labels.getLabel;
import static net.azib.ipscan.gui.util.LayoutHelper.formData;

/**
 * GUI for initialization of RangeFeeder.
 * 
 * @author Anton Keks
 */
public class RangeFeederGUI extends AbstractFeederGUI {
	
	private Label ipRangeLabel;
	private Text startIPText;
	
	private Label toLabel;
	private Text endIPText;
	private boolean isEndIPUnedited = true;
	private boolean modifyListenersDisabled = false;
		
	private Label hostnameLabel;
	private Text hostnameText;
	
	private Button ipUpButton;
	
	private Combo netmaskCombo;
	
	public RangeFeederGUI(Composite parent) {
		super(parent);
		feeder = new RangeFeeder();
	}

	public void initialize(int rowHeight) {
        ipRangeLabel = new Label(this, SWT.NONE);
        startIPText = new Text(this, SWT.BORDER);
        toLabel = new Label(this, SWT.NONE);
        endIPText = new Text(this, SWT.BORDER);
        hostnameLabel = new Label(this, SWT.NONE);
        hostnameText = new Text(this, SWT.BORDER);
		ipUpButton = new Button(this, SWT.NONE);
        netmaskCombo = new Combo(this, SWT.NONE);

		// the longest possible IP
        startIPText.setText("255.255.255.255xx");
        int textWidth = startIPText.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        startIPText.setText("");
        
        ipRangeLabel.setText(getLabel("feeder.range") + ":");
		hostnameLabel.setAlignment(SWT.RIGHT);
		ipRangeLabel.pack();
		hostnameLabel.setText(getLabel("feeder.range.hostname") + ":");
		ipRangeLabel.setAlignment(SWT.RIGHT);
		hostnameLabel.pack();

		int ipHostWidth = Math.max(hostnameLabel.getBounds().width, ipRangeLabel.getBounds().width);
		ipRangeLabel.setLayoutData(formData(ipHostWidth, SWT.DEFAULT, null, new FormAttachment(hostnameLabel, 0, SWT.RIGHT), new FormAttachment(startIPText, 0, SWT.CENTER), null));
		hostnameLabel.setLayoutData(formData(ipHostWidth, SWT.DEFAULT, new FormAttachment(0), null, new FormAttachment(hostnameText, 0, SWT.CENTER), null));

		startIPText.setLayoutData(formData(textWidth, SWT.DEFAULT, new FormAttachment(ipRangeLabel), null, new FormAttachment(0), null));
        startIPText.addModifyListener(new StartIPModifyListener());
        
        toLabel.setText(getLabel("feeder.range.to"));
        toLabel.setLayoutData(formData(new FormAttachment(startIPText), null, new FormAttachment(startIPText, 0, SWT.CENTER), null));
                
        endIPText.setLayoutData(formData(textWidth, SWT.DEFAULT, new FormAttachment(toLabel), null, new FormAttachment(0), null));
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
		hostnameText.setLayoutData(formData(textWidth, SWT.DEFAULT, new FormAttachment(startIPText, 0, SWT.LEFT), null, new FormAttachment(startIPText), null));
		hostnameText.setToolTipText(getLabel("feeder.range.hostname.tooltip"));
		
		Listener netmaskResetListener = new NetmaskResetListener();
		startIPText.addListener(SWT.Modify, netmaskResetListener);
		endIPText.addListener(SWT.Modify, netmaskResetListener);

		ipUpButton.setImage(new Image(getDisplay(), Labels.getInstance().getImageAsStream("button.ipUp.img")));
		ipUpButton.setText(getLabel("button.ipUp"));
		ipUpButton.addSelectionListener(hostnameListener);
		ipUpButton.setLayoutData(formData(new FormAttachment(hostnameText), null, new FormAttachment(hostnameText, Platform.MAC_OS ? -4 : 0, SWT.TOP), null));

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
		netmaskCombo.setLayoutData(formData(new FormAttachment(ipUpButton, 5), new FormAttachment(endIPText, 0, SWT.RIGHT), new FormAttachment(startIPText), new FormAttachment(hostnameText, 0, SWT.BOTTOM)));
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
				InetAddress netmask = InetAddressUtils.parseNetmask(netmaskString);
				InetAddress startIP = InetAddress.getByName(startIPText.getText());
				
				modifyListenersDisabled = true;
				startIPText.setText(InetAddressUtils.startRangeByNetmask(startIP, netmask).getHostAddress());
				endIPText.setText(InetAddressUtils.endRangeByNetmask(startIP, netmask).getHostAddress());
				modifyListenersDisabled = false;
				isEndIPUnedited = false;
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
}
