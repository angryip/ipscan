package net.azib.ipscan.gui;

import dagger.Module;
import dagger.Provides;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Andriy Kryvtsun
 */
@Module
public class MenuModule {

	@Provides @Named("commandsMenu") @Singleton
	public Menu provideCommandsMenu(@Named("mainShell") Shell parent) {
		return new MainMenu.CommandsMenu(parent);
	}
}
