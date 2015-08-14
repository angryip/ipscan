package net.azib.ipscan.config;

import dagger.Component;
import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.gui.MacApplicationMenu;
import net.azib.ipscan.gui.MainMenu;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.MenuModule;

import javax.inject.Singleton;

@Component(modules = {
		Config.class,
		Labels.class,
		PluginLoader.class,
		ComponentRegistry.class,
		MenuModule.class
})
@Singleton
public interface MainComponent {
	MainWindow getMainWindow();
	MacApplicationMenu getMacApplicationMenu();
	CommandLineProcessor getCommandLineProcessor();
}
