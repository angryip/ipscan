package net.azib.ipscan.gui.menu;

import net.azib.ipscan.gui.actions.CommandsMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;

/**
 * OpenersMenu wrapper for type-safety
 */
public class OpenersMenu extends AbstractMenu {

	@Inject
	public OpenersMenu(Shell parent,
					   CommandsMenuActions.EditOpeners editOpenersListener,
					   CommandsMenuActions.ShowOpenersMenu showOpenersMenuListener) {

		super(parent);

		initMenuItem(this, "menu.commands.open.edit", null, null, editOpenersListener);
		initMenuItem(this, null, null, null, null);

		addListener(SWT.Show, showOpenersMenuListener);

		// run the listener to populate the menu initially and initialize accelerators
		Event e = new Event();
		e.widget = this;
		showOpenersMenuListener.handleEvent(e);
	}

	@Override
	public String getId() {
		return "menu.commands.open";
	}
}
