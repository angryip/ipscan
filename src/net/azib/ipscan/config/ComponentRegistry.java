/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.core.net.Pinger;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.di.Injector;
import net.azib.ipscan.exporters.*;
import net.azib.ipscan.fetchers.*;

/**
 * This class is the dependency injection configuration
 * 
 * @author Anton Keks
 */
public class ComponentRegistry {
	public void register(Injector i) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		i.register(IPFetcher.class, PingFetcher.class, PingTTLFetcher.class, HostnameFetcher.class, PortsFetcher.class);
		i.register(MACFetcher.class, (MACFetcher) Class.forName(MACFetcher.class.getPackage().getName() +
				(Platform.WINDOWS ? ".WinMACFetcher" : Platform.LINUX ? ".LinuxMACFetcher" : ".UnixMACFetcher")).newInstance());
		i.register(CommentFetcher.class, FilteredPortsFetcher.class, WebDetectFetcher.class, HTTPSenderFetcher.class,
			NetBIOSInfoFetcher.class, PacketLossFetcher.class, HTTPProxyFetcher.class, MACVendorFetcher.class);
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
			new PluginLoader().getClasses().forEach(c -> {
				var plugin = i.require(c);
				if (Pinger.class.isAssignableFrom(c))
					pingerRegistry.register(plugin.getId(), (Class) c);
			});
		}
		return i;
	}
}
