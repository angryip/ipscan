/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.di.Injector;
import net.azib.ipscan.exporters.CSVExporter;
import net.azib.ipscan.exporters.IPListExporter;
import net.azib.ipscan.exporters.TXTExporter;
import net.azib.ipscan.exporters.XMLExporter;
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
		i.register(TXTExporter.class, CSVExporter.class, XMLExporter.class, IPListExporter.class);
	}

	public Injector init() throws Exception {
		return init(true);
	}

	public Injector init(boolean withGUI) throws Exception {
		Injector i = new Injector();
		new ConfigModule().register(i);
		new ComponentRegistry().register(i);
		if (withGUI) {
			new GUIRegistry().register(i);
			new PluginLoader().getClasses().forEach(i::require);
		}
		return i;
	}
}
