/**
 * 
 */
package net.azib.ipscan.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

/**
 * Labels class for localization, based on PropertyResourceBundle.
 * It adds some special methods for loading of images by IDs.
 * 
 * It is a singleton, so use getInstance() in order to use this class.
 * 
 * Use initialize() to create an instance of this class.
 * 
 * @author anton
 */
public final class Labels {
	
	private static Labels instance;

	PropertyResourceBundle labels;
	Locale locale;
	
	static {
		// this is needed for Visual Editor to display 
		// labels at design time
		initialize(new Locale("en"));
	}
	
	private Labels() {
		// private constructor
	}
	
	public static final Labels getInstance() {
		return instance;
	}
	
	/**
	 * Initialized the internal locale-specific data.
	 * The files Labels_LANG.txt and Labels.txt are searched for
	 * in the same package as this class.
	 * This method must be called prior to using this class.
	 */
	public static void initialize(Locale locale) {
		if (instance != null && locale.equals(instance.locale)) {
			// do not reload locale, because it was already initialized in the static block
			return;
		}
		// create a new instance
		instance = new Labels();
		
		instance.locale = locale;
		InputStream labelsStream = null;
		labelsStream = Labels.class.getClassLoader().getResourceAsStream("Labels_" + locale.getLanguage().toUpperCase() + ".txt");
		if (labelsStream == null) {
			labelsStream = Labels.class.getClassLoader().getResourceAsStream("Labels.txt");
		}
		if (labelsStream == null) {
			throw new MissingResourceException("Labels not found!", Labels.class.getName(), "Labels");
		}
		try {
			instance.labels = new PropertyResourceBundle(labelsStream);
		}
		catch (IOException e) {
			throw new MissingResourceException(e.toString(), Labels.class.getName(), "Labels");
		}
	}
	
	/**
	 * Retrieves an InputStream to load the image, specified by a key in resource file.
	 * @param key
	 */
	public InputStream getImageAsStream(String key) {
		String imagePath = labels.getString(key);
		return getClass().getClassLoader().getResourceAsStream(imagePath);
	}
	
	/**
	 * Retrieves a String specified by the label key
	 * @param key
	 */
	public String get(String key) {
		return labels.getString(key);
	}
	
	/**
	 * A shortened form of Labels.getLabel()
	 */
	public static String getLabel(String key) {
		return getInstance().get(key);
	}
	
}
