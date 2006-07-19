/**
 * 
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.feeders.Feeder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class of feeder GUI classes.
 * 
 * @author anton
 */
public abstract class AbstractFeederGUI extends Composite {
	
	protected Feeder feeder;

	public AbstractFeederGUI(Composite parent) {
		super(parent, SWT.NONE);
		initialize();
	}

	protected abstract void initialize();
	
	/**
	 * Initializes a Feeder instance using the parameters, provided by the GUI.
	 * @return initialized feeder instance
	 */
	public abstract Feeder getFeeder();
	
	/**
	 * @return the feeder name
	 */
	public String getFeederName() {
		return Labels.getInstance().getString(feeder.getLabel());
	}
	
	/**
	 * For internal usage, returns the feeder-specific label
	 */
	protected String getStringLabel(String name) {
		return Labels.getInstance().getString(feeder.getLabel() + '.' + name);
	}

	/**
	 * @return the feeder's name and the information about its current settings
	 */
	public String getInfo() {
		// getFeeder() will probably double-initialize the feeder, but it is safer
		return getFeederName() + ": " + getFeeder().getInfo();
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
