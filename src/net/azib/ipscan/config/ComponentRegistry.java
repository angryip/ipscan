/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.exporters.*;
import net.azib.ipscan.feeders.FeederCreator;
import net.azib.ipscan.feeders.FeederRegistry;
import net.azib.ipscan.fetchers.*;
import net.azib.ipscan.gui.MacApplicationMenu;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.SWTAwareStateMachine;
import net.azib.ipscan.gui.feeders.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * This class is the dependency injection configuration using the Pico Container.
 * 
 * @author Anton Keks
 */
@Module
public class ComponentRegistry {

	private PicoContainer container;

	private boolean containerStarted;

	@Inject public ComponentRegistry(@Named("plugins") List<Class> plugins) {
		MutablePicoContainer container = new DefaultPicoContainer();
		this.container = container;

		if (Platform.MAC_OS)
			container.registerComponentImplementation(MacApplicationMenu.class);

		registerComponentImplementations(container, plugins);
	}

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

	@Provides public AbstractFeederGUI[] feeders(RangeFeederGUI f1, RandomFeederGUI f2, FileFeederGUI f3) {
		return new AbstractFeederGUI[] {f1, f2, f3};
	}

	@Provides public Exporter[] exporters(TXTExporter e1, CSVExporter e2, XMLExporter e3, IPListExporter e4) {
		return new Exporter[] {e1, e2, e3, e4};
	}

	@Provides public Fetcher[] fetchers(IPFetcher f1, PingFetcher f2, PingTTLFetcher f3, HostnameFetcher f4, PortsFetcher f5,
										  	   FilteredPortsFetcher f6, WebDetectFetcher f7, HTTPSenderFetcher f8, CommentFetcher f9,
									 		   NetBIOSInfoFetcher f10, MACFetcher f11, MACVendorFetcher f12) {
		return new Fetcher[] {f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12};
	}

	@Provides MACFetcher selectMacFetcher() {
		return Platform.WINDOWS ? new WinMACFetcher() : new UnixMACFetcher();
	}


	private static void registerComponentImplementations(MutablePicoContainer container, List<Class> classes) {
		for (Class clazz: classes) {
			container.registerComponentImplementation(clazz);
		}
	}

	private void start() {
		if (!containerStarted) {
			containerStarted = true;
			container.start();
		}
	}

	public MainWindow getMainWindow() {
		// initialize all startable components
		start();
		// initialize and return the main window
		return (MainWindow) container.getComponentInstance(MainWindow.class);
	}

	public CommandLineProcessor getCommandLineProcessor() {
		start();
		return (CommandLineProcessor) container.getComponentInstance(CommandLineProcessor.class);
	}
}
