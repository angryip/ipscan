package net.azib.ipscan.config;

import dagger.Component;
import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.gui.MacApplicationMenu;
import net.azib.ipscan.gui.MainWindow;

import javax.inject.Singleton;

@Component(modules = {
		ConfigModule.class,
		PluginLoader.class,
		ComponentRegistry.class
})
@Singleton
public interface MainComponent {
	MainWindow createMainWindow();
	MacApplicationMenu createMacApplicationMenu();
	CommandLineProcessor createCommandLineProcessor();
}
