/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.di.Injector;
import net.azib.ipscan.exporters.CSVExporter;
import net.azib.ipscan.exporters.IPListExporter;
import net.azib.ipscan.exporters.TXTExporter;
import net.azib.ipscan.exporters.XMLExporter;
import net.azib.ipscan.feeders.FeederRegistry;
import net.azib.ipscan.fetchers.*;
import net.azib.ipscan.gui.SWTAwareStateMachine;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.feeders.FileFeederGUI;
import net.azib.ipscan.gui.feeders.RandomFeederGUI;
import net.azib.ipscan.gui.feeders.RangeFeederGUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

/**
 * This class is the dependency injection configuration
 * 
 * @author Anton Keks
 */
public class ComponentRegistry {
	public void register(Injector i) {
		Display display = Display.getDefault();
		i.register(Display.class, display);
		Shell shell = new Shell();
		i.register(Shell.class, shell);
		i.register(Menu.class, "mainMenu", new Menu(shell, SWT.BAR));
		i.register(Composite.class, "feederArea", new Composite(shell, SWT.NONE));
		Composite controlsArea = new Composite(shell, SWT.NONE);
		i.register(Composite.class, "controlsArea", controlsArea);
		i.register(Combo.class, "feederSelectionCombo", new Combo(controlsArea, SWT.READ_ONLY));
		i.register(Button.class, "startStopButton", new Button(controlsArea, SWT.NONE));
		SWTAwareStateMachine stateMachine = new SWTAwareStateMachine(display);
		i.register(SWTAwareStateMachine.class, stateMachine);
		i.register(StateMachine.class, stateMachine);
		i.register(RangeFeederGUI.class, RandomFeederGUI.class, FileFeederGUI.class);
		i.register(TXTExporter.class, CSVExporter.class, XMLExporter.class, IPListExporter.class);
		i.register(IPFetcher.class, PingFetcher.class, PingTTLFetcher.class, HostnameFetcher.class, PortsFetcher.class,
			FilteredPortsFetcher.class, WebDetectFetcher.class, HTTPSenderFetcher.class, CommentFetcher.class,
			NetBIOSInfoFetcher.class, PacketLossFetcher.class, HTTPProxyFetcher.class);
		i.register(MACFetcher.class, Platform.WINDOWS ? new WinMACFetcher() : new UnixMACFetcher());
		i.register(MACVendorFetcher.class);
		i.register(FeederRegistry.class, i.require(FeederGUIRegistry.class));
	}

	public Injector init() {
		Injector i = new Injector();
		new ConfigModule().register(i);
		new ComponentRegistry().register(i);
		new PluginLoader().getClasses().forEach(i::require);
		return i;
	}
}
