/**
 * 
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.Scanner;
import net.azib.ipscan.core.ScannerThreadFactory;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.FetcherRegistryImpl;
import net.azib.ipscan.gui.MainMenu;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.StatusBar;
import net.azib.ipscan.gui.MainMenu.CommandsMenu;
import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.OpenerLauncher;
import net.azib.ipscan.gui.actions.StartStopScanningAction;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.feeders.FileFeederGUI;
import net.azib.ipscan.gui.feeders.RandomFeederGUI;
import net.azib.ipscan.gui.feeders.RangeFeederGUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
 * This class is a dependency injection configuration
 * using the Pico Container.
 *
 * @author anton
 */
public class GUIComponentContainer {
	
	private PicoContainer container;
	
	public GUIComponentContainer() {
		MutablePicoContainer container = new DefaultPicoContainer();
		this.container = container;
		
		ComponentParameter anyComponentParameter = new ComponentParameter();
		
		// non-GUI
		container.registerComponentImplementation(FetcherRegistry.class, FetcherRegistryImpl.class);
		container.registerComponentImplementation(ScanningResultList.class);
		container.registerComponentImplementation(Scanner.class);
		container.registerComponentImplementation(ScannerThreadFactory.class);
		
		// GUI follows
		
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
		container.registerComponentImplementation("feederSelectionCombo", Combo.class, new Parameter[] {
			new ComponentParameter("controlsArea"),
			new ConstantParameter(new Integer(SWT.READ_ONLY))});		
		
		// GUI Feeders
		container.registerComponentImplementation(FeederGUIRegistry.class);
		Parameter[] feederGUIParameters = new Parameter[] {new ComponentParameter("feederArea")};
		container.registerComponentImplementation(RangeFeederGUI.class, RangeFeederGUI.class, feederGUIParameters);
		container.registerComponentImplementation(RandomFeederGUI.class, RandomFeederGUI.class, feederGUIParameters);
		container.registerComponentImplementation(FileFeederGUI.class, FileFeederGUI.class, feederGUIParameters);
		
		container.registerComponentImplementation(OpenerLauncher.class);
		container.registerComponentImplementation(MainWindow.class, MainWindow.class, new Parameter[] {
			new ComponentParameter("mainShell"), 
			new ComponentParameter("feederArea"),
			new ComponentParameter("controlsArea"),
			new ComponentParameter("feederSelectionCombo"),
			new ComponentParameter("startStopButton"),
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter});
		container.registerComponentImplementation(ResultTable.class, ResultTable.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			anyComponentParameter,
			anyComponentParameter,
			anyComponentParameter});
		container.registerComponentImplementation(StatusBar.class, StatusBar.class, new Parameter[] {
			new ComponentParameter("mainShell")});
		
		container.registerComponentImplementation(MainMenu.class, MainMenu.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			new ComponentParameter("mainMenu"),
			new ComponentParameter("commandsMenu"),
			new ConstantParameter(container)});
		container.registerComponentImplementation(MainMenu.ColumnsMenu.class, MainMenu.ColumnsMenu.class, new Parameter[] {
			new ComponentParameter("mainShell"),
			anyComponentParameter});

		// various actions / listener
		container.registerComponentImplementation(StartStopScanningAction.class);
		container.registerComponentImplementation(ColumnsActions.SortBy.class);
	}
	
	public MainWindow createMainWindow() {
		// initialize the main menu
		container.getComponentInstance(MainMenu.class);
		// initialize and return the main window
		return (MainWindow) container.getComponentInstance(MainWindow.class);
	}

}
