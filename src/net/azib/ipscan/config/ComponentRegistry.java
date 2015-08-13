/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.core.Plugin;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.exporters.*;
import net.azib.ipscan.feeders.FeederCreator;
import net.azib.ipscan.feeders.FeederRegistry;
import net.azib.ipscan.fetchers.*;
import net.azib.ipscan.gui.SWTAwareStateMachine;
import net.azib.ipscan.gui.feeders.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is the dependency injection configuration using the Pico Container.
 * 
 * @author Anton Keks
 */
@Module
public class ComponentRegistry {
	private boolean containerStarted;

	@Provides public Display getDisplay() {
		return Display.getDefault();
	}

	@Provides @Named("mainShell") public Shell createMainShell() {
		return new Shell();
	}

	@Provides @Named("mainMenu") public Menu createMainMenu(@Named("mainShell") Shell mainShell) {
		return new Menu(mainShell, SWT.BAR);
	}

	@Provides @Named("feederArea") public Composite createFeederArea(@Named("mainShell") Shell mainShell) {
		return new Composite(mainShell, SWT.NONE);
	}

	@Provides @Named("controlsArea") public Composite createControlsArea(@Named("mainShell") Shell mainShell) {
		return new Composite(mainShell, SWT.NONE);
	}

	@Provides @Named("startStopButton") public Button createStartStopButton(@Named("controlsArea") Composite controlsArea) {
		return new Button(controlsArea, SWT.NONE);
	}

	@Provides @Named("feederSelectionCombo") public Combo createFeederSelectionCombo(@Named("controlsArea") Composite controlsArea) {
		return new Combo(controlsArea, SWT.READ_ONLY);
	}

	@Provides StateMachine stateMachine(SWTAwareStateMachine stateMachine) {
		return stateMachine;
	}

	@Provides FeederRegistry<? extends FeederCreator> feederRegistry(FeederGUIRegistry feederRegistry) {
		return feederRegistry;
	}

	@Provides public List<AbstractFeederGUI> feeders(RangeFeederGUI f1, RandomFeederGUI f2, FileFeederGUI f3) {
		return Arrays.asList(f1, f2, f3);
	}

	@Provides public List<Exporter> exporters(@Named("plugins") List<Class> plugins, TXTExporter e1, CSVExporter e2, XMLExporter e3, IPListExporter e4) {
		return addPlugins(Arrays.<Exporter>asList(e1, e2, e3, e4), Exporter.class, plugins);
	}

	@Provides public List<Fetcher> fetchers(@Named("plugins") List<Class> plugins,
											IPFetcher f1, PingFetcher f2, PingTTLFetcher f3, HostnameFetcher f4, PortsFetcher f5,
											FilteredPortsFetcher f6, WebDetectFetcher f7, HTTPSenderFetcher f8, CommentFetcher f9,
											NetBIOSInfoFetcher f10, MACFetcher f11, MACVendorFetcher f12) {
		return addPlugins(Arrays.<Fetcher>asList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12), Fetcher.class, plugins);
	}

	@Provides MACFetcher selectMacFetcher() {
		return Platform.WINDOWS ? new WinMACFetcher() : new UnixMACFetcher();
	}

	@SuppressWarnings("unchecked")
	private <T extends Plugin> List<T> addPlugins(List<T> original, Class<T> type, @Named("plugins") List<Class> classes) {
		List<T> result = new ArrayList<T>(original);
		for (Class clazz: classes) {
			try {
				if (type.isAssignableFrom(clazz))
					result.add((T)clazz.newInstance());
			}
			catch (Exception e) {
				throw new RuntimeException("Cannot instantiate plugin with default constructor: " + clazz.getName());
			}
		}
		return result;
	}

	// TODO: implement start with Dagger
	private void start() {
		if (!containerStarted) {
			containerStarted = true;
//			container.start();
		}
	}
}
