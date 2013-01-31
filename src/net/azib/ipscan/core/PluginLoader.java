package net.azib.ipscan.core;

import net.azib.ipscan.config.LoggerFactory;
import org.picocontainer.MutablePicoContainer;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 * Loads plugins using two ways:
 * <ul>
 * <li>
 *     Classes listed in <code>ipscan.plugins</code> system property.
 * 	   The classes themselves must reside on the application's classpath - useful for development.
 * </li>
 * <li>
 *     Jar files residing in the same directory as ipscan's binary file (jar or exe).
 *     These jar files must have their classes listed in <code>META-INF/MANIFEST.MF</code> as <code>IPScan-Plugins</code>.
 * </li>
 * </ul>
 * In either way, all plugins must implement {@link net.azib.ipscan.core.Plugin} and one or more of the concrete interfaces.
 */
public class PluginLoader {
    static final Logger LOG = LoggerFactory.getLogger();

    public void addTo(MutablePicoContainer container) {
		loadPluginsSpecifiedInSystemProperties(container);
		loadPluginJars(container);
    }

	private void loadPluginsSpecifiedInSystemProperties(MutablePicoContainer container) {
		String plugins = System.getProperty("ipscan.plugins");
		if (plugins != null) {
			loadPluginClasses(container, getClass().getClassLoader(), plugins);
		}
	}

	private void loadPluginClasses(MutablePicoContainer container, ClassLoader classLoader, String csvNames) {
		String[] classes = csvNames.split("\\s*,\\s*");
		for (String className : classes) {
			try {
				Class clazz = Class.forName(className, true, classLoader);
				if (Plugin.class.isAssignableFrom(clazz))
					container.registerComponentImplementation(clazz);
				else
					LOG.warning("Plugin class " + clazz.getName() + " is not assignable to " + Plugin.class.getName());
			}
			catch (ClassNotFoundException e) {
				LOG.warning("Unable to load plugin: " + className);
			}
		}
	}

	private void loadPluginJars(MutablePicoContainer container) {
		String ownPath = PluginLoader.class.getResource("PluginLoader.class").getFile();
		if (ownPath.startsWith("file:")) ownPath = ownPath.substring("file:".length());
		if (ownPath.indexOf('!') >= 0) ownPath = ownPath.substring(0, ownPath.indexOf('!'));
		final File ownFile = new File(ownPath);

		File[] jars = ownFile.getParentFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar") && !name.equals(ownFile.getName());
			}
		});

		for (File jar : jars) {
			try {
				URLClassLoader loader = new URLClassLoader(new URL[] {jar.toURI().toURL()}, PluginLoader.class.getClassLoader());
				Manifest manifest = new Manifest(loader.getResourceAsStream("META-INF/MANIFEST.MF"));

				String className = manifest.getMainAttributes().getValue("IPScan-Plugin");
				if (className != null) loadPluginClasses(container, loader, className);

				String classNames = manifest.getMainAttributes().getValue("IPScan-Plugins");
				if (classNames != null) loadPluginClasses(container, loader, classNames);
			}
			catch (Exception e) {
				LOG.warning("Failed to load plugin jar " + jar + ": " + e);
			}
		}
	}
}
