/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.core.Scanner;
import net.azib.ipscan.core.ScannerDispatcherThreadFactory;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.exporters.*;
import net.azib.ipscan.fetchers.*;
import net.azib.ipscan.gui.*;
import net.azib.ipscan.gui.MainMenu.CommandsMenu;
import net.azib.ipscan.gui.MainWindow.FeederSelectionCombo;
import net.azib.ipscan.gui.actions.*;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.feeders.FileFeederGUI;
import net.azib.ipscan.gui.feeders.RandomFeederGUI;
import net.azib.ipscan.gui.feeders.RangeFeederGUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.logging.Logger;

/**
 * This class is the dependency injection configuration using the Pico Container.
 * 
 * @author Anton Keks
 */
public class ComponentRegistry {

	private PicoContainer container;

	private boolean containerStarted;

	public ComponentRegistry() {
		MutablePicoContainer container = new DefaultPicoContainer();
		this.container = container;

		ComponentParameter anyComponentParameter = new ComponentParameter();

		// non-GUI
		Config globalConfig = Config.getConfig();
		container.registerComponentInstance(globalConfig.getPreferences());
		container.registerComponentInstance(globalConfig.forScanner());
		container.registerComponentInstance(globalConfig.forGUI());
		container.registerComponentInstance(globalConfig.forOpeners());
		container.registerComponentInstance(globalConfig.forFavorites());
		container.registerComponentInstance(Labels.getInstance());
		container.registerComponentImplementation(CommentsConfig.class);
		container.registerComponentImplementation(ConfigDetector.class);

		container.registerComponentImplementation(ExporterRegistry.class);
		container.registerComponentImplementation(TXTExporter.class);
		container.registerComponentImplementation(CSVExporter.class);
		container.registerComponentImplementation(XMLExporter.class);
		container.registerComponentImplementation(IPListExporter.class);

		container.registerComponentImplementation(FetcherRegistry.class, FetcherRegistry.class);
		container.registerComponentImplementation(IPFetcher.class);
		container.registerComponentImplementation(PingFetcher.class);
		container.registerComponentImplementation(PingTTLFetcher.class);
		container.registerComponentImplementation(HostnameFetcher.class);
		container.registerComponentImplementation(PortsFetcher.class);
		container.registerComponentImplementation(FilteredPortsFetcher.class);
		container.registerComponentImplementation(WebDetectFetcher.class);
		container.registerComponentImplementation(HTTPSenderFetcher.class);
		container.registerComponentImplementation(CommentFetcher.class);
		container.registerComponentImplementation(NetBIOSInfoFetcher.class);
		if (Platform.WINDOWS) container.registerComponentImplementation(WinMACFetcher.class);
		else container.registerComponentImplementation(UnixMACFetcher.class);
		container.registerComponentImplementation(MACVendorFetcher.class);

		container.registerComponentImplementation(PingerRegistry.class, PingerRegistry.class);
		container.registerComponentImplementation(ScanningResultList.class);
		container.registerComponentImplementation(Scanner.class);
		container.registerComponentImplementation(SWTAwareStateMachine.class);
		container.registerComponentImplementation(ScannerDispatcherThreadFactory.class);
		container.registerComponentImplementation(CommandLineProcessor.class);

		// GUI follows (TODO: move GUI to a separate place)

		// Some "shared" GUI components
		container.registerComponentInstance(Display.getDefault());
		container.registerComponentImplementation("mainShell", Shell.class);
		container.registerComponentImplementation("mainMenu", Menu.class, new Parameter[] {
				new ComponentParameter("mainShell"), new ConstantParameter(SWT.BAR) });
		container.registerComponentImplementation("commandsMenu", CommandsMenu.class);

		container.registerComponentImplementation("feederArea", Composite.class, new Parameter[] {
				new ComponentParameter("mainShell"), new ConstantParameter(SWT.NONE) });
		container.registerComponentImplementation("controlsArea", Composite.class, new Parameter[] {
				new ComponentParameter("mainShell"), new ConstantParameter(SWT.NONE) });
		container.registerComponentImplementation("startStopButton", Button.class, new Parameter[] {
				new ComponentParameter("controlsArea"), new ConstantParameter(SWT.NONE) });
		container.registerComponentImplementation("feederSelectionCombo", FeederSelectionCombo.class,
				new Parameter[] { new ComponentParameter("controlsArea") });

		// GUI Feeders
		container.registerComponentImplementation(FeederGUIRegistry.class);
		Parameter[] feederGUIParameters = new Parameter[] { new ComponentParameter("feederArea") };
		container.registerComponentImplementation(RangeFeederGUI.class, RangeFeederGUI.class, feederGUIParameters);
		container.registerComponentImplementation(RandomFeederGUI.class, RandomFeederGUI.class, feederGUIParameters);
		container.registerComponentImplementation(FileFeederGUI.class, FileFeederGUI.class, feederGUIParameters);

		container.registerComponentImplementation(OpenerLauncher.class);
		container.registerComponentImplementation(MainWindow.class, MainWindow.class, new Parameter[] {
				new ComponentParameter("mainShell"), anyComponentParameter, new ComponentParameter("feederArea"),
				new ComponentParameter("controlsArea"), new ComponentParameter("feederSelectionCombo"),
				new ComponentParameter("startStopButton"), anyComponentParameter, anyComponentParameter,
				anyComponentParameter, anyComponentParameter, anyComponentParameter, anyComponentParameter,
				anyComponentParameter, anyComponentParameter });
		container.registerComponentImplementation(ResultTable.class, ResultTable.class, new Parameter[] {
				new ComponentParameter("mainShell"), anyComponentParameter, anyComponentParameter,
				anyComponentParameter, anyComponentParameter, anyComponentParameter, anyComponentParameter });
		container.registerComponentImplementation(StatusBar.class, StatusBar.class, new Parameter[] {
				new ComponentParameter("mainShell"), anyComponentParameter, anyComponentParameter, anyComponentParameter, anyComponentParameter });

		container.registerComponentImplementation(MainMenu.class, MainMenu.class, new Parameter[] {
				new ComponentParameter("mainShell"), new ComponentParameter("mainMenu"),
				new ComponentParameter("commandsMenu"), anyComponentParameter, new ConstantParameter(container) });
		container.registerComponentImplementation(MainMenu.ColumnsMenu.class, MainMenu.ColumnsMenu.class,
				new Parameter[] { new ComponentParameter("mainShell"), anyComponentParameter, anyComponentParameter,
						anyComponentParameter });

		container.registerComponentImplementation(AboutDialog.class);
		container.registerComponentImplementation(PreferencesDialog.class);
		container.registerComponentImplementation(ConfigDetectorDialog.class);
		container.registerComponentImplementation(SelectFetchersDialog.class);
		container.registerComponentImplementation(DetailsWindow.class);
		container.registerComponentImplementation(StatisticsDialog.class);

		// various actions / listeners
		container.registerComponentImplementation(StartStopScanningAction.class);
		container.registerComponentImplementation(ColumnsActions.SortBy.class);
		container.registerComponentImplementation(ColumnsActions.FetcherPreferences.class);
		container.registerComponentImplementation(ColumnsActions.AboutFetcher.class);
		container.registerComponentImplementation(ColumnsActions.ColumnClick.class);
		container.registerComponentImplementation(ColumnsActions.ColumnResize.class);
		container.registerComponentImplementation(CommandsMenuActions.Details.class);
		container.registerComponentImplementation(ToolsActions.Preferences.class);
		container.registerComponentImplementation(ToolsActions.ChooseFetchers.class);
		container.registerComponentImplementation(HelpMenuActions.CheckVersion.class);

		if (Platform.MAC_OS) {
			// initialize mac-specific stuff
			try {
				container.registerComponentImplementation(Class
						.forName("net.azib.ipscan.platform.mac.MacApplicationMenu"));
			}
			catch (Exception e) {
				Logger.getLogger(getClass().getName()).warning("Cannot initialize MacApplicationMenu: " + e);
			}
		}

        new PluginLoader().addTo(container);
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
