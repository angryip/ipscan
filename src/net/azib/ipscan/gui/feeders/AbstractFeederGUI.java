/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FeederCreator;
import net.azib.ipscan.util.InetAddressUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Base class of feeder GUI classes.
 * 
 * @author Anton Keks
 */
public abstract class AbstractFeederGUI extends Composite implements FeederCreator {
	static final Logger LOG = LoggerFactory.getLogger();
	
	protected Feeder feeder;

	public AbstractFeederGUI(Composite parent) {
		super(parent, SWT.NONE);
		setVisible(false);
		initialize();
	}

	public abstract void initialize();
		
	/**
	 * @return the feeder id
	 */
	public String getFeederId() {
		return feeder.getId();
	}

	/**
	 * @return the feeder name
	 */
	public String getFeederName() {
		return feeder.getName();
	}
	
	/**
	 * @return the feeder's name and the information about its current settings
	 */
	public String getInfo() {
		return getFeederName() + ": " + createFeeder().getInfo();
	}
	
	private static final Object localResolveLock = new Object();
	/** Cached name of local host **/
	private static String localName;
	/** Cached address of local host **/
	private static InterfaceAddress localInterface;
	
	/**
	 * Asynchronously resolves localhost's name and address and then populates the specified fields.
	 * The idea is to show GUI faster.
	 */
	protected void asyncFillLocalHostInfo(final Text hostnameText, final Text ipText) {
		new Thread(() -> {
			// this method is called for multiple Feeders simultaneously
			synchronized (localResolveLock) {
				if (localInterface == null) {
					localInterface = InetAddressUtils.getLocalInterface();
					try {
						localName = InetAddress.getLocalHost().getHostName();
					}
					catch (UnknownHostException e) {
						localName = localInterface.getAddress().getHostName();
					}
				}
				Display.getDefault().asyncExec(() -> {
					// fill the IP and hostname fields with local hostname and IP addresses
					if ("".equals(hostnameText.getText()))
						hostnameText.setText(localName);
					if ("".equals(ipText.getText())) {
						ipText.setText(localInterface.getAddress().getHostAddress());
						afterLocalHostInfoFilled(localInterface);
					}
				});
			}
		}).start();
	}

	public Feeder createFeeder() {
		return createFeeder();
	}

	protected void afterLocalHostInfoFilled(InterfaceAddress localInterface) {
	}
}
