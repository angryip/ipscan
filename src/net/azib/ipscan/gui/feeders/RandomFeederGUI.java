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
import net.azib.ipscan.feeders.RandomFeeder;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * GUI for initialization of RandomFeeder
 *
 * @author anton
 */
public class RandomFeederGUI extends AbstractFeederGUI {
	
	private Label ipPrototypeLabel;
	private Text ipPrototypeText;
	
	private Label ipMaskLabel;
	private Combo ipMaskCombo;
	
	private Label hostnameLabel;
	private Text hostnameText;
	
	private Button ipUpButton;
	
	private Label countLabel;
	private Spinner countSpinner;

	
	public RandomFeederGUI(Composite parent) {
		super(parent);
	}

	protected void initialize() {
		feeder = new RandomFeeder();
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 3;
		formLayout.marginHeight = 3;
		formLayout.spacing = 4;
		setLayout(formLayout);
		
        ipPrototypeLabel = new Label(this, SWT.NONE);
        ipPrototypeText = new Text(this, SWT.BORDER);
        ipMaskLabel = new Label(this, SWT.NONE);
        ipMaskCombo = new Combo(this, SWT.NONE);
        hostnameText = new Text(this, SWT.BORDER);
        hostnameLabel = new Label(this, SWT.NONE);
		ipUpButton = new Button(this, SWT.NONE);
        countLabel = new Label(this, SWT.NONE);
        countSpinner = new Spinner(this, SWT.BORDER);
        
        ipPrototypeLabel.setText(getStringLabel("prototype"));
        FormData formData = new FormData();
		formData.right = new FormAttachment(hostnameLabel, 0, SWT.RIGHT);
        formData.top = new FormAttachment(ipPrototypeText, 0, SWT.CENTER);
        ipPrototypeLabel.setLayoutData(formData);
        
		formData = new FormData(105, SWT.DEFAULT);
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(ipPrototypeLabel);
        ipPrototypeText.setLayoutData(formData);
        
        ipMaskLabel.setText(getStringLabel("mask"));
        formData = new FormData();
        formData.left = new FormAttachment(ipPrototypeText, 3);
        formData.top = new FormAttachment(ipPrototypeText, 0, SWT.CENTER);
        ipMaskLabel.setLayoutData(formData);
        
		ipMaskCombo.setVisibleItemCount(10);
		// Warning: IPv4 specific netmasks
		ipMaskCombo.add("255...128");
		ipMaskCombo.add("255...0");
		ipMaskCombo.add("255..0.0");
		ipMaskCombo.add("255.0.0.0");
		ipMaskCombo.add("0.0.0.0");
		ipMaskCombo.add("255..0.255");
		ipMaskCombo.add("255.0.0.255");
		ipMaskCombo.select(3);
		formData = new FormData(105, SWT.DEFAULT);
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(ipMaskLabel);
		formData.bottom = new FormAttachment(ipPrototypeText, 0, SWT.BOTTOM);
		ipMaskCombo.setLayoutData(formData);
        
		HostnameSelectionListener hostnameSelectionListener = new HostnameSelectionListener();
        try {
			hostnameText.setText(InetAddress.getLocalHost().getHostName());
		} 
        catch (UnknownHostException e1) {
			// leave hostnameText empty
		}
        hostnameText.addTraverseListener(hostnameSelectionListener);
        formData = new FormData(105, SWT.DEFAULT);
		formData.top = new FormAttachment(ipPrototypeText);
		formData.left = new FormAttachment(ipPrototypeText, 0, SWT.LEFT);
		hostnameText.setLayoutData(formData);
        
        hostnameLabel.setText(getStringLabel("hostname"));
        formData = new FormData();
        formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(hostnameText, 0, SWT.CENTER);
		hostnameLabel.setLayoutData(formData);
		
		ipUpButton.setImage(new Image(getDisplay(), Labels.getInstance().getImageAsStream("button.ipUp.img")));
		ipUpButton.addSelectionListener(hostnameSelectionListener);
		formData = new FormData(35, SWT.DEFAULT);
		formData.top = new FormAttachment(ipPrototypeText);
		formData.left = new FormAttachment(hostnameText);
		formData.bottom = new FormAttachment(hostnameText, 0, SWT.BOTTOM);
		ipUpButton.setLayoutData(formData);
		
		countLabel.setText(getStringLabel("count"));
        formData = new FormData();
		formData.left = new FormAttachment(ipUpButton, 3);
		formData.top = new FormAttachment(hostnameLabel, 0, SWT.TOP);
		countLabel.setLayoutData(formData);
		
		countSpinner.setSelection(100);
		countSpinner.setMaximum(100000);
		countSpinner.setMinimum(1);
        formData = new FormData();
		formData.left = new FormAttachment(countLabel);
		formData.top = new FormAttachment(ipUpButton, 0, SWT.CENTER);
		formData.right = new FormAttachment(ipMaskCombo, 0, SWT.RIGHT);
//		formData.bottom = new FormAttachment(ipUpButton, 0, SWT.BOTTOM);
		countSpinner.setLayoutData(formData);
		
		// fill the IP text with local IP address
		hostnameSelectionListener.widgetSelected(null);
		                        
		pack();
	}

	public Feeder getFeeder() {
		((RandomFeeder)feeder).initialize(ipPrototypeText.getText(), ipMaskCombo.getText(), countSpinner.getSelection());
		return feeder;
	}
	
	public String serialize() {
		return ipPrototypeText.getText() + ":::" + ipMaskCombo.getText() + ":::" + countSpinner.getSelection();
	}

	public void unserialize(String serialized) {
		String[] parts = serialized.split(":::");
		ipPrototypeText.setText(parts[0]);
		ipMaskCombo.setText(parts[1]);
		countSpinner.setSelection(Integer.parseInt(parts[2]));
	}
	
	private final class HostnameSelectionListener implements SelectionListener, TraverseListener {
		public void widgetDefaultSelected(SelectionEvent event) {
			widgetSelected(event);
		}

		public void widgetSelected(SelectionEvent event) {
			try {
				String hostname = hostnameText.getText();
				String address = InetAddressUtils.getAddressByName(hostname);
				ipPrototypeText.setText(address);
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

}
