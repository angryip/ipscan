package net.azib.ipscan.config;

import dagger.Component;
import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.gui.MacApplicationMenu;
import net.azib.ipscan.gui.MainWindow;

@Component(modules = {
		Config.class,
		Labels.class,
		PluginLoader.class,
		ComponentRegistry.class
})
public interface MainComponent {
	MainWindow getMainWindow();
	MacApplicationMenu getMacApplicationMenu();
	CommandLineProcessor getCommandLineProcessor();
}
