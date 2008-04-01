/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.feeders.Feeder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class of feeder GUI classes.
 * 
 * @author Anton Keks
 */
public abstract class AbstractFeederGUI extends Composite {
	
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
}
