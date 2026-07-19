/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.core.PluginTrustVerifier;
import net.azib.ipscan.core.net.Pinger;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.di.Injector;
import net.azib.ipscan.exporters.*;
import net.azib.ipscan.fetchers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * This class is the dependency injection configuration
 * 
 * @author Anton Keks
 */
public class ComponentRegistry {
	private static final Logger LOG = Logger.getLogger(ComponentRegistry.class.getName());
	private static final String TRUSTED_PLUGINS_KEY = "trustedPlugins";
	private static final String REJECTED_PLUGINS_KEY = "rejectedPlugins";

	public void register(Injector i) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		i.register(IPFetcher.class, PingFetcher.class, PingTTLFetcher.class, HostnameFetcher.class, PortsFetcher.class);
		i.register(MACFetcher.class, (MACFetcher) Class.forName(MACFetcher.class.getPackage().getName() +
				(Platform.WINDOWS ? ".WinMACFetcher" : Platform.LINUX ? ".LinuxMACFetcher" : ".UnixMACFetcher")).newInstance());
		i.register(CommentFetcher.class, FilteredPortsFetcher.class, WebDetectFetcher.class, HTTPSenderFetcher.class,
			NetBIOSInfoFetcher.class, PacketLossFetcher.class, HTTPProxyFetcher.class, MACVendorFetcher.class, OpenerColumnFetcher.class, OpenerLaunchFetcher.class);
		i.register(TXTExporter.class, CSVExporter.class, XMLExporter.class, IPListExporter.class, SQLExporter.class);
	}

	public Injector init() throws Exception {
		return init(true);
	}

	public Injector init(boolean withGUI) throws Exception {
		var i = new Injector();
		new ConfigModule().register(i);
		new ComponentRegistry().register(i);
		if (withGUI) {
			new GUIRegistry().register(i);
			var pingerRegistry = i.require(PingerRegistry.class);
			var preferences = i.require(Config.class).getPreferences();
			new PluginLoader().getClasses(createTrustVerifier(preferences)).forEach(c -> {
				var plugin = i.require(c);
				if (Pinger.class.isAssignableFrom(c))
					pingerRegistry.register(plugin.getId(), (Class) c);
			});
		}
		return i;
	}

	PluginTrustVerifier createTrustVerifier(Preferences preferences) {
		return (jarFile, classNames) -> {
			var trusted = loadTrustedPlugins(preferences);
			var rejected = loadRejectedPlugins(preferences);
			try {
				var canonicalPath = jarFile.getCanonicalPath();
				if (trusted.contains(canonicalPath)) return true;
				if (rejected.contains(canonicalPath)) return false;
			}
			catch (IOException e) {
				// fall through to prompt
			}

			var result = promptUserTrust(jarFile, classNames);
			if (result == SWT.YES) {
				saveTrustedPlugin(preferences, jarFile);
				return true;
			}
			else if (result == SWT.NO) {
				saveRejectedPlugin(preferences, jarFile);
			}
			return false;
		};
	}

	private int promptUserTrust(File jarFile, String classNames) {
		var display = Display.getDefault();
		var result = new int[] { SWT.CANCEL };
		display.syncExec(() -> {
			var shell = display.getActiveShell();
			if (shell == null) shell = new Shell(display);
			var box = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			box.setText(Labels.getLabel("plugin.trust.title"));
			var message = Labels.getLabel("plugin.trust.message")
					.replace("%JAR", jarFile.getName())
					.replace("%PATH", jarFile.getAbsolutePath())
					.replace("%CLASSES", classNames);
			box.setMessage(message);
			result[0] = box.open();
		});
		return result[0];
	}

	private Set<String> loadTrustedPlugins(Preferences preferences) {
		var value = preferences.get(TRUSTED_PLUGINS_KEY, "");
		if (value.isEmpty()) return new HashSet<>();
		return new HashSet<>(Arrays.asList(value.split("\\|")));
	}

	private void saveTrustedPlugin(Preferences preferences, File jarFile) {
		try {
			var trusted = loadTrustedPlugins(preferences);
			trusted.add(jarFile.getCanonicalPath());
			preferences.put(TRUSTED_PLUGINS_KEY, String.join("|", trusted));
		}
		catch (IOException e) {
			LOG.warning("Failed to save trusted plugin: " + jarFile);
		}
	}

	private Set<String> loadRejectedPlugins(Preferences preferences) {
		var value = preferences.get(REJECTED_PLUGINS_KEY, "");
		if (value.isEmpty()) return new HashSet<>();
		return new HashSet<>(Arrays.asList(value.split("\\|")));
	}

	private void saveRejectedPlugin(Preferences preferences, File jarFile) {
		try {
			var rejected = loadRejectedPlugins(preferences);
			rejected.add(jarFile.getCanonicalPath());
			preferences.put(REJECTED_PLUGINS_KEY, String.join("|", rejected));
		}
		catch (IOException e) {
			LOG.warning("Failed to save rejected plugin: " + jarFile);
		}
	}
}
