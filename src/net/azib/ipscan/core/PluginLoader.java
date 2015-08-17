package net.azib.ipscan.core;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.config.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 * Loads plugins using three ways:
 * <ul>
 * <li>
 *     Classes listed in <code>ipscan.plugins</code> system property.
 * 	   The classes themselves must reside on the application's classpath - useful for development.
 * </li>
 * <li>
 *     Jar files residing in the same directory as ipscan's binary file (jar or exe).
 *     Plugin jar files must have their classes listed in <code>META-INF/MANIFEST.MF</code> as <code>IPScan-Plugins</code>.
 * </li>
 * <li>
 *     Jar files residing in the $HOME/.ipscan directory.
 * </li>
 * </ul>
 * In either way, all plugins must implement {@link net.azib.ipscan.core.Plugin} and one or more of the concrete interfaces.
 */
@Module
public class PluginLoader {
    private static final Logger LOG = LoggerFactory.getLogger();

	@Provides @Singleton
	public List<Class<? extends Plugin>> getClasses() {
		List<Class<? extends Plugin>> container = new ArrayList<Class<? extends Plugin>>();

		loadPluginsSpecifiedInSystemProperties(container);
		loadPluginJars(container, getOwnFile());
		loadPluginJars(container, new File(System.getProperty("user.home"), ".ipscan/placeholder"));

		return container;
	}

	void loadPluginsSpecifiedInSystemProperties(List<Class<? extends Plugin>> container) {
		String plugins = System.getProperty("ipscan.plugins");
		if (plugins != null) {
			loadPluginClasses(container, getClass().getClassLoader(), plugins);
		}
	}

	private void loadPluginClasses(List<Class<? extends Plugin>> container, ClassLoader classLoader, String csvNames) {
		String[] classes = csvNames.split("\\s*,\\s*");
		for (String className : classes) {
			try {
				Class clazz = Class.forName(className, true, classLoader);
				if (Plugin.class.isAssignableFrom(clazz))
					container.add(clazz);
				else
					LOG.warning("Plugin class " + clazz.getName() + " is not assignable to " + Plugin.class.getName());
			}
			catch (ClassNotFoundException e) {
				LOG.warning("Unable to load plugin: " + className);
			}
		}
	}

	void loadPluginJars(List<Class<? extends Plugin>> container, final File ownFile) {
		if (!ownFile.getParentFile().exists()) return;

		File[] jars = ownFile.getParentFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar") && !name.equals(ownFile.getName());
			}
		});

		PluginClassLoader loader = new PluginClassLoader();
		for (File jar : jars) {
			try {
				JarFile jarFile = new JarFile(jar);
				Manifest manifest = jarFile.getManifest();
				if (manifest == null) continue;
				jarFile.close();

				String className = manifest.getMainAttributes().getValue("IPScan-Plugin");
				if (className != null) {
					loader.addURL(jar.toURI().toURL());
					loadPluginClasses(container, loader, className);
				}

				String classNames = manifest.getMainAttributes().getValue("IPScan-Plugins");
				if (classNames != null) {
					loader.addURL(jar.toURI().toURL());
					loadPluginClasses(container, loader, classNames);
				}
			}
			catch (Exception e) {
				LOG.warning("Failed to load plugin jar " + jar + ": " + e);
			}
		}
	}

	private File getOwnFile() {
		return getClassLocation(getClass());
	}

	File getClassLocation(Class clazz) {
		String ownPath = clazz.getResource(clazz.getSimpleName() + ".class").getFile();
		if (ownPath.startsWith("file:")) ownPath = ownPath.substring("file:".length());
		if (ownPath.indexOf('!') >= 0) ownPath = ownPath.substring(0, ownPath.indexOf('!'));
		return new File(ownPath);
	}

	static class PluginClassLoader extends URLClassLoader {
		PluginClassLoader() {
			super(new URL[0], PluginLoader.class.getClassLoader());
		}

		@Override protected void addURL(URL url) {
			super.addURL(url); //make protected method accessible to PluginLoader
		}
	}
}
