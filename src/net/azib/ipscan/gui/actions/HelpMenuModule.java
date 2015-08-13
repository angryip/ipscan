package net.azib.ipscan.gui.actions;

import dagger.Module;
import dagger.Provides;
import org.eclipse.swt.widgets.Listener;

import javax.inject.Named;

/**
 * Created by englishman on 8/12/15.
 */
@Module
public class HelpMenuModule {
	@Provides
	@Named("about")
	Listener provideAbout(HelpMenuActions.About action) {
		return action;
	}
}
