/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.gui.MacApplicationMenu;
import net.azib.ipscan.gui.MainWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * This class is the dependency injection configuration using the Pico Container.
 * 
 * @author Anton Keks
 */
@Module
public class ComponentRegistry {

	private PicoContainer container;

	private boolean containerStarted;

	@Inject
	public ComponentRegistry(java.util.List<Class> plugins) {
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

	@Component(modules = ComponentRegistry.class)
	public interface MainWindowComponent {
		MainWindow getMainWindow();
		CommandLineProcessor getCommandLineProcessor();
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
