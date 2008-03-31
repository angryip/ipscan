/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.Scanner;
import net.azib.ipscan.core.ScannerDispatcherThreadFactory;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.core.net.PingerRegistryImpl;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.exporters.CSVExporter;
import net.azib.ipscan.exporters.ExporterRegistry;
import net.azib.ipscan.exporters.IPListExporter;
import net.azib.ipscan.exporters.TXTExporter;
import net.azib.ipscan.exporters.XMLExporter;
import net.azib.ipscan.fetchers.CommentFetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.FetcherRegistryImpl;
import net.azib.ipscan.fetchers.FilteredPortsFetcher;
import net.azib.ipscan.fetchers.HostnameFetcher;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.NetBIOSInfoFetcher;
import net.azib.ipscan.fetchers.PingFetcher;
import net.azib.ipscan.fetchers.PingTTLFetcher;
import net.azib.ipscan.fetchers.PortsFetcher;
import net.azib.ipscan.fetchers.WebDetectFetcher;
import net.azib.ipscan.gui.AboutDialog;
import net.azib.ipscan.gui.ConfigDetectorDialog;
import net.azib.ipscan.gui.DetailsWindow;
import net.azib.ipscan.gui.MainMenu;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.PreferencesDialog;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.SelectFetchersDialog;
import net.azib.ipscan.gui.StatisticsDialog;
import net.azib.ipscan.gui.StatusBar;
import net.azib.ipscan.gui.MainMenu.CommandsMenu;
import net.azib.ipscan.gui.MainWindow.FeederSelectionCombo;
import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.CommandsActions;
import net.azib.ipscan.gui.actions.HelpActions;
import net.azib.ipscan.gui.actions.OpenerLauncher;
import net.azib.ipscan.gui.actions.StartStopScanningAction;
import net.azib.ipscan.gui.actions.ToolsActions;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.feeders.FileFeederGUI;
import net.azib.ipscan.gui.feeders.RandomFeederGUI;
import net.azib.ipscan.gui.feeders.RangeFeederGUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * This class is the dependency injection configuration
 * using the Pico Container.
 *
 * @author Anton Keks
 */
public class ComponentRegistry {
	
	private PicoContainer container;
	
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
		
		container.registerComponentImplementation(FetcherRegistry.class, FetcherRegistryImpl.class);
		container.registerComponentImplementation(IPFetcher.class);
		container.registerComponentImplementation(PingFetcher.class);
		container.registerComponentImplementation(PingTTLFetcher.class);
		container.registerComponentImplementation(HostnameFetcher.class);
		container.registerComponentImplementation(PortsFetcher.class);
		container.registerComponentImplementation(FilteredPortsFetcher.class);
		container.registerComponentImplementation(WebDetectFetcher.class);
		container.registerComponentImplementation(CommentFetcher.class);
		container.registerComponentImplementation(NetBIOSInfoFetcher.class);
		
		container.registerComponentImplementation(PingerRegistry.class, PingerRegistryImpl.class);
		container.registerComponentImplementation(ScanningResultList.class);
		container.registerComponentImplementation(Scanner.class);
		container.registerComponentImplementation(StateMachine.class);
		container.registerComponentImplementation(ScannerDispatcherThreadFactory.class);
		
		// GUI follows (TODO: move GUI to a separate place)
		
		// Some "shared" GUI components
		container.registerComponentImplementation("mainShell", Shell.class);
		container.registerComponentImplementation("mainMenu", Menu.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			new ConstantParameter(new Integer(SWT.BAR))});
		container.registerComponentImplementation("commandsMenu", CommandsMenu.class);
		
		container.registerComponentImplementation("feederArea", Composite.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			new ConstantParameter(new Integer(SWT.NONE))});
		container.registerComponentImplementation("controlsArea", Composite.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			new ConstantParameter(new Integer(SWT.NONE))});
		container.registerComponentImplementation("startStopButton", Button.class, new Parameter[] {
			new ComponentParameter("controlsArea"),
			new ConstantParameter(new Integer(SWT.NONE))});
		container.registerComponentImplementation("feederSelectionCombo", FeederSelectionCombo.class, new Parameter[] {
			new ComponentParameter("controlsArea"),
			anyComponentParameter});		
		
		// GUI Feeders
		container.registerComponentImplementation(FeederGUIRegistry.class);
		Parameter[] feederGUIParameters = new Parameter[] {new ComponentParameter("feederArea")};
		container.registerComponentImplementation(RangeFeederGUI.class, RangeFeederGUI.class, feederGUIParameters);
		container.registerComponentImplementation(RandomFeederGUI.class, RandomFeederGUI.class, feederGUIParameters);
		container.registerComponentImplementation(FileFeederGUI.class, FileFeederGUI.class, feederGUIParameters);
		
		container.registerComponentImplementation(OpenerLauncher.class);
		container.registerComponentImplementation(MainWindow.class, MainWindow.class, new Parameter[] {
			new ComponentParameter("mainShell"), 
			anyComponentParameter,
			new ComponentParameter("feederArea"),
			new ComponentParameter("controlsArea"),
			new ComponentParameter("feederSelectionCombo"),
			new ComponentParameter("startStopButton"),
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter});
		container.registerComponentImplementation(ResultTable.class, ResultTable.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter});
		container.registerComponentImplementation(StatusBar.class, StatusBar.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			anyComponentParameter,
			anyComponentParameter});
		
		container.registerComponentImplementation(MainMenu.class, MainMenu.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			new ComponentParameter("mainMenu"),
			new ComponentParameter("commandsMenu"),
			anyComponentParameter,
			new ConstantParameter(container)});
		container.registerComponentImplementation(MainMenu.ColumnsMenu.class, MainMenu.ColumnsMenu.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter});
		
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
		container.registerComponentImplementation(CommandsActions.Details.class);
		container.registerComponentImplementation(ToolsActions.Preferences.class);
		container.registerComponentImplementation(ToolsActions.ChooseFetchers.class);
		container.registerComponentImplementation(ToolsActions.TableSelection.class);
		container.registerComponentImplementation(HelpActions.CheckVersion.class);

		if (Platform.MAC_OS) {
			// initialize mac-specific stuff
			container.registerComponentImplementation(net.azib.ipscan.gui.mac.MacApplicationMenu.class);
		}
	}
	
	public MainWindow createMainWindow() {
		// initialize all startable components
		container.start();
		// initialize and return the main window
		return (MainWindow) container.getComponentInstance(MainWindow.class);
	}

}
