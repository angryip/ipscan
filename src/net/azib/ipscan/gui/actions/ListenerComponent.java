package net.azib.ipscan.gui.actions;

import dagger.Component;
import dagger.Provides;
import org.eclipse.swt.widgets.Listener;

import javax.inject.Named;
import javax.inject.Qualifier;

/**
 * Created by englishman on 8/12/15.
 */
@Component(modules = HelpMenuModule.class)
public interface ListenerComponent {
	@Named("about")
	Listener getHelpMenuAboutListener();
}
