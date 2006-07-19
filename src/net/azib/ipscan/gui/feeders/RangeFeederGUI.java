/**
 * 
 */
package net.azib.ipscan.gui.feeders;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.InetAddressUtils;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FeederException;
import net.azib.ipscan.feeders.RangeFeeder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * GUI for initialization of RangeFeeder.
 * 
 * @author anton
 */
public class RangeFeederGUI extends AbstractFeederGUI {

	private Label ipRangeLabel;
	private Text startIPText;
	
	private Label toLabel;
	private Text endIPText;
	private boolean isEndIPUnedited = true;
		
	private Label hostnameLabel;
	private Text hostnameText;
	
	private Button ipUpButton;
	
	private Combo netmaskCombo;
	
	public RangeFeederGUI(Composite parent) {
		super(parent);
	}

	protected void initialize() {
		feeder = new RangeFeeder();
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 3;
		formLayout.marginHeight = 3;
		formLayout.marginBottom = 2;
		formLayout.spacing = 4;
		setLayout(formLayout);
		
        ipRangeLabel = new Label(this, SWT.NONE);
        startIPText = new Text(this, SWT.BORDER);
        toLabel = new Label(this, SWT.NONE);
        endIPText = new Text(this, SWT.BORDER);
        hostnameLabel = new Label(this, SWT.NONE);
        hostnameText = new Text(this, SWT.BORDER);
		ipUpButton = new Button(this, SWT.NONE);
        netmaskCombo = new Combo(this, SWT.NONE);
        
        ipRangeLabel.setText(getStringLabel("startIP"));
        FormData formData = new FormData();
		formData.right = new FormAttachment(hostnameLabel, 0, SWT.RIGHT);
        formData.top = new FormAttachment(startIPText, 0, SWT.CENTER);
        ipRangeLabel.setLayoutData(formData);
        
		formData = new FormData(105, SWT.DEFAULT);
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(ipRangeLabel);
        startIPText.setLayoutData(formData);
        startIPText.addModifyListener(new StartIPModifyListener());
        
        toLabel.setText(getStringLabel("endIP"));
        formData = new FormData();
        formData.left = new FormAttachment(startIPText);
        formData.top = new FormAttachment(startIPText, 0, SWT.CENTER);
        toLabel.setLayoutData(formData);
        
        try {
			endIPText.setText(InetAddress.getLocalHost().getHostAddress());
		} 
        catch (UnknownHostException e) {
			// leave endIPText empty
		}
		formData = new FormData(105, SWT.DEFAULT);
		formData.left = new FormAttachment(toLabel);
        endIPText.setLayoutData(formData);
        endIPText.addKeyListener(new EndIPKeyListener());
        
		HostnameSelectionListener hostnameSelectionListener = new HostnameSelectionListener();
        try {
			hostnameText.setText(InetAddress.getLocalHost().getHostName());
		} 
        catch (UnknownHostException e1) {
			// leave hostnameText empty
		}
        hostnameText.addTraverseListener(hostnameSelectionListener);
        formData = new FormData(105, SWT.DEFAULT);
		formData.top = new FormAttachment(startIPText);
		formData.left = new FormAttachment(startIPText, 0, SWT.LEFT);
		hostnameText.setLayoutData(formData);
        
        hostnameLabel.setText(getStringLabel("hostname"));
        formData = new FormData();
        formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(hostnameText, 0, SWT.CENTER);
		hostnameLabel.setLayoutData(formData);
		
		ipUpButton.setImage(new Image(getDisplay(), Labels.getInstance().getImageAsStream("button.ipUp.img")));
		ipUpButton.addSelectionListener(hostnameSelectionListener);
		formData = new FormData(35, SWT.DEFAULT);
		formData.top = new FormAttachment(endIPText);
		formData.left = new FormAttachment(hostnameText);
		formData.bottom = new FormAttachment(hostnameText, 0, SWT.BOTTOM);
		ipUpButton.setLayoutData(formData);
        
        netmaskCombo.setText(getStringLabel("netmask"));
		netmaskCombo.setVisibleItemCount(10);
		// Warning: IPv4 specific netmasks
		netmaskCombo.add("255...240");
		netmaskCombo.add("255...224");
		netmaskCombo.add("255...192");
		netmaskCombo.add("255...128");
		netmaskCombo.add("255...0");
		netmaskCombo.add("255..0.0");
		netmaskCombo.add("255.0.0.0");
		NetmaskSelectionListener netmaskSelectionListener = new NetmaskSelectionListener();
		netmaskCombo.addSelectionListener(netmaskSelectionListener);
		netmaskCombo.addTraverseListener(netmaskSelectionListener);
		formData = new FormData();
		formData.top = new FormAttachment(startIPText);
		formData.left = new FormAttachment(ipUpButton, 5);
		formData.right = new FormAttachment(endIPText, 0, SWT.RIGHT);
		formData.bottom = new FormAttachment(hostnameText, 0, SWT.BOTTOM);
		netmaskCombo.setLayoutData(formData);
		
		// fill the IP text with local IP address
		hostnameSelectionListener.widgetSelected(null);
                
		pack();
	}

	public Feeder getFeeder() {
		((RangeFeeder)feeder).initialize(startIPText.getText(), endIPText.getText());
		return feeder;
	}
	
	public String serialize() {
		return startIPText.getText() + ":::" + endIPText.getText();
	}

	public void unserialize(String serialized) {
		String[] parts = serialized.split(":::");
		startIPText.setText(parts[0]);
		endIPText.setText(parts[1]);
	}

	private final class EndIPKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			isEndIPUnedited = false;
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	private final class StartIPModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			if (isEndIPUnedited) {
				endIPText.setText(startIPText.getText());
			}
		}
	}

	private final class HostnameSelectionListener implements SelectionListener, TraverseListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			try {
				String hostname = hostnameText.getText();
				String address = InetAddressUtils.getAddressByName(hostname);
				isEndIPUnedited = true;
				startIPText.setText(address);
			} 
			catch (UnknownHostException e) {
				throw new FeederException("invalidHostname");
			}
		}
		
		public void keyTraversed(TraverseEvent e) {
			if (e.detail == SWT.TRAVERSE_RETURN) {
				widgetSelected(null);
				e.doit = false;
			}
		}
	}

	private final class NetmaskSelectionListener implements SelectionListener, TraverseListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			try {
				String netmaskString = netmaskCombo.getText();
				InetAddress netmask = InetAddressUtils.parseNetmask(netmaskString);
				InetAddress startIP = InetAddress.getByName(startIPText.getText());
				
				// TODO: retrieve all addresses of the hostname and let user choose the correct one
		
				startIPText.setText(InetAddressUtils.startRangeByNetmask(startIP, netmask).getHostAddress());
				endIPText.setText(InetAddressUtils.endRangeByNetmask(startIP, netmask).getHostAddress());
				isEndIPUnedited = false;
			}
			catch (UnknownHostException e) {
				throw new FeederException("invalidNetmask");
			}
		}

		public void keyTraversed(TraverseEvent e) {
			if (e.detail == SWT.TRAVERSE_RETURN) {
				widgetSelected(null);
				e.doit = false;
			}
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
