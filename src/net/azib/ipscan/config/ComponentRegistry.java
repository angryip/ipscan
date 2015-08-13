/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import dagger.Module;
import net.azib.ipscan.core.*;
import net.azib.ipscan.core.Scanner;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.exporters.*;
import net.azib.ipscan.fetchers.*;
import net.azib.ipscan.gui.*;
import net.azib.ipscan.gui.MainMenu.CommandsMenu;
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

import javax.inject.Inject;

/**
 * This class is the dependency injection configuration using the Pico Container.
 * 
 * @author Anton Keks
 */
public class ComponentRegistry {

	private PicoContainer container;

	private boolean containerStarted;

	@Inject
	public ComponentRegistry(java.util.List<Class> plugins) {
		MutablePicoContainer container = new DefaultPicoContainer();
		this.container = container;

		ComponentParameter anyComponentParameter = new ComponentParameter();

		// non-GUI
		Config globalConfig = Config.getConfig();
		container.registerComponentInstance(globalConfig);
		container.registerComponentInstance(globalConfig.getPreferences());
		container.registerComponentInstance(globalConfig.forScanner());
		container.registerComponentInstance(globalConfig.forGUI());
		container.registerComponentInstance(globalConfig.forOpeners());
		container.registerComponentInstance(globalConfig.forFavorites());
		container.registerComponentInstance(Labels.getInstance());
		container.registerComponentImplementation(CommentsConfig.class);

		container.registerComponentInstance(DaggerExporterComponent.create().get());
		container.registerComponentInstance(DaggerFetcherComponent.create().get());

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
		container.registerComponentImplementation("feederSelectionCombo", Combo.class,
				new Parameter[] { new ComponentParameter("controlsArea"), new ConstantParameter(SWT.READ_ONLY) });

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
				new Parameter[] { new ComponentParameter("mainShell"), anyComponentParameter, anyComponentParameter, anyComponentParameter });
		if (Platform.MAC_OS)
			container.registerComponentImplementation(MacApplicationMenu.class);

		container.registerComponentImplementation(AboutDialog.class);
		container.registerComponentImplementation(PreferencesDialog.class);
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

		registerComponentImplementations(container, plugins);
	}

	private static void registerComponentImplementations(MutablePicoContainer container, java.util.List<Class> classes) {
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
