/**
 * 
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.gui.MainMenu;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.StatusBar;
import net.azib.ipscan.gui.actions.OpenerLauncher;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.feeders.FileFeederGUI;
import net.azib.ipscan.gui.feeders.RandomFeederGUI;
import net.azib.ipscan.gui.feeders.RangeFeederGUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.BasicComponentParameter;
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
		
		container.registerComponentInstance(container);
		
		// non-GUI
		container.registerComponentImplementation(ScanningResultList.class);
		
		// GUI follows
		
		// Some "shared" GUI components
		container.registerComponentImplementation("mainShell", Shell.class);
		container.registerComponentImplementation("feederArea", Composite.class, new Parameter[] {
			new BasicComponentParameter("mainShell"),
			new ConstantParameter(new Integer(SWT.NONE))});
		container.registerComponentImplementation("controlsArea", Composite.class, new Parameter[] {
			new BasicComponentParameter("mainShell"),
			new ConstantParameter(new Integer(SWT.NONE))});
		container.registerComponentImplementation("feederSelectionCombo", Combo.class, new Parameter[] {
			new BasicComponentParameter("controlsArea"),
			new ConstantParameter(new Integer(SWT.READ_ONLY))});
		
		// GUI Feeders
		container.registerComponentImplementation(FeederGUIRegistry.class);
		Parameter[] feederGUIParameters = new Parameter[] {new BasicComponentParameter("feederArea")};
		container.registerComponentImplementation(RangeFeederGUI.class, RangeFeederGUI.class, feederGUIParameters);
		container.registerComponentImplementation(RandomFeederGUI.class, RandomFeederGUI.class, feederGUIParameters);
		container.registerComponentImplementation(FileFeederGUI.class, FileFeederGUI.class, feederGUIParameters);
		
		container.registerComponentImplementation(OpenerLauncher.class);
		container.registerComponentImplementation(MainWindow.class, MainWindow.class, new Parameter[] {
			new BasicComponentParameter("mainShell"), 
			new BasicComponentParameter("feederArea"),
			new BasicComponentParameter("controlsArea"),
			new BasicComponentParameter("feederSelectionCombo"),
			BasicComponentParameter.BASIC_DEFAULT,
			BasicComponentParameter.BASIC_DEFAULT,
			BasicComponentParameter.BASIC_DEFAULT,
			BasicComponentParameter.BASIC_DEFAULT});
		container.registerComponentImplementation(ResultTable.class, ResultTable.class, new Parameter[] {
			new BasicComponentParameter("mainShell"), 
			BasicComponentParameter.BASIC_DEFAULT});
		container.registerComponentImplementation(StatusBar.class, StatusBar.class, new Parameter[] {
			new BasicComponentParameter("mainShell")});
		container.registerComponentImplementation(MainMenu.class, MainMenu.class, new Parameter[] {
			new BasicComponentParameter("mainShell"), 
			BasicComponentParameter.BASIC_DEFAULT});
	}
	
	public MainWindow createMainWindow() {
		return (MainWindow) container.getComponentInstance(MainWindow.class);
	}

}
