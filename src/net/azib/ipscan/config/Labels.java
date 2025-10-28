/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Labels class for localization, based on PropertyResourceBundle.
 * It adds some special methods for loading of images by IDs.
 * 
 * It is a singleton, so use getInstance() in order to use this class.
 * Use initialize() to create an instance of this class.
 * 
 * @author Anton Keks
 */
public final class Labels {
	public static final String[] LANGUAGES = {"system", "en", "ru", "de", "hu", "lt", "es", "fi", "fr", "it", "ga_IE", "ku", "tr", "gr", "pt_BR", "zh_CN", "zh_TW"};
	private static final Logger LOG = Logger.getLogger(Labels.class.getName());
	private static Labels instance;

	Locale locale;
	Properties labels = new Properties();
	Properties fallback = new Properties();

	static {
		// this is needed for Visual Editor to display 
		// labels at design time
		initialize(Locale.getDefault());
	}
	
	Labels(Locale locale) {
		this.locale = locale;
		load(getClass().getClassLoader());
	}

	public static Labels getInstance() {
		return instance;
	}
	
	/**
	 * Initializes the internal locale-specific data.
	 * The files messages_lang.properties and messages.properties are searched for from the classpath.
	 */
	public static void initialize(Locale locale) {
		if (instance != null && locale.equals(instance.locale)) {
			// do not reload locale, because it was already initialized in the static block
			return;
		}
		instance = new Labels(locale);
	}

	public void load(ClassLoader loader) {
		try (InputStream in = loader.getResourceAsStream("messages.properties")) {
			if (in != null) fallback.load(new InputStreamReader(in, UTF_8));
		}
		catch (IOException e) {
			throw new MissingResourceException(e.toString(), Labels.class.getName(), "messages");
		}

		try (InputStream in = loader.getResourceAsStream("messages_" + locale.toString() + ".properties")) {
			labels.load(new InputStreamReader(in, UTF_8));
		}
		catch (Exception e) {
			try (InputStream in = loader.getResourceAsStream("messages_" + locale.getLanguage() + ".properties")) {
				labels.load(new InputStreamReader(in, UTF_8));
			}
			catch (Exception e2) {
				labels = fallback;
			}
		}
	}

	public String getOrNull(String key) {
		var text = labels.getProperty(key);
		if (text == null) {
			text = fallback.getProperty(key);
			if (text != null && !key.startsWith("language.")) LOG.warning("Used fallback label for " + key);
		}
		return text;
	}

	public String get(String key) {
		var text = getOrNull(key);
		if (text == null) {
			text = key;
			LOG.warning("Missing label for " + key);
		}
		return text;
	}

	public static String getLabel(String key) {
		return getInstance().get(key);
	}
}
