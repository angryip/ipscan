/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.core.InetAddressUtils;
import net.azib.ipscan.feeders.Feeder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Base class of feeder GUI classes.
 * 
 * @author Anton Keks
 */
public abstract class AbstractFeederGUI extends Composite {
	
	static final Logger LOG = LoggerFactory.getLogger();
	
	protected Feeder feeder;

	public AbstractFeederGUI(Composite parent) {
		super(parent, SWT.NONE);
		setVisible(false);
		initialize();
	}

	protected abstract void initialize();
	
	/**
	 * Initializes a Feeder instance using the parameters, provided by the GUI.
	 * @return initialized feeder instance
	 */
	public abstract Feeder createFeeder();
	
	/**
	 * @return the feeder name
	 */
	public String getFeederName() {
		return feeder.getName();
	}
	
	/**
	 * For internal usage, returns the feeder-specific label
	 * TODO: remove this method
	 */
	protected String getStringLabel(String name) {
		return Labels.getLabel(feeder.getId() + '.' + name);
	}

	/**
	 * @return the feeder's name and the information about its current settings
	 */
	public String getInfo() {
		return getFeederName() + ": " + createFeeder().getInfo();
	}
	
	/**
	 * @return serialized settings to a String
	 */
	public abstract String serialize();
	
	/**
	 * Restores previously serialized settings.
	 * @param serialized
	 */
	public abstract void unserialize(String serialized);

	/**
	 * Asynchronously resolves localhost's name and address and then populates the specified fields.
	 * The idea is to show GUI faster.
	 */
	protected void asyncFillLocalHostInfo(final Text hostnameText, final Text ipText) {
		new Thread() {
			public void run() {
				// fill the IP and hostname fields with local hostname and IP addresses
				try {
					String localhostName = InetAddress.getLocalHost().getHostName();
					final InetAddress localhost = InetAddressUtils.getAddressByName(localhostName);
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							if ("".equals(hostnameText.getText()))
								hostnameText.setText(localhost.getHostName());
							if ("".equals(ipText.getText()))
								ipText.setText(localhost.getHostAddress());
						}
					});
				}
				catch (UnknownHostException e) {
					// don't report any errors on initialization, leave fields empty
					LOG.fine(e.toString());
				}
			}
		}.start();		
	}
}
