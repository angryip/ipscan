package net.azib.ipscan.gui.menu;

import net.azib.ipscan.gui.actions.GotoMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GotoMenu extends AbstractMenu {

	@Inject
	public GotoMenu(Shell parent,
					GotoMenuActions.NextAliveHost nextAliveHost,
					GotoMenuActions.NextHostWithInfo nextHostWithInfo,
					GotoMenuActions.NextDeadHost nextDeadHost,
					GotoMenuActions.PrevAliveHost prevAliveHost,
					GotoMenuActions.PrevHostWithInfo prevHostWithInfo,
					GotoMenuActions.PrevDeadHost prevDeadHost,
					GotoMenuActions.Find find) {

		super(parent);

		initMenuItem(this, "menu.goto.next.aliveHost", "Ctrl+H", SWT.MOD1 | 'H', nextAliveHost);
		initMenuItem(this, "menu.goto.next.openPort", "Ctrl+J", SWT.MOD1 | 'J', nextHostWithInfo);
		initMenuItem(this, "menu.goto.next.deadHost", "Ctrl+K", SWT.MOD1 | 'K', nextDeadHost);
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.goto.prev.aliveHost", "Ctrl+Shift+H", SWT.MOD1 | SWT.MOD2 | 'H', prevAliveHost);
		initMenuItem(this, "menu.goto.prev.openPort", "Ctrl+Shift+J", SWT.MOD1 | SWT.MOD2 | 'J', prevHostWithInfo);
		initMenuItem(this, "menu.goto.prev.deadHost", "Ctrl+Shift+K", SWT.MOD1 | SWT.MOD2 | 'K', prevDeadHost);
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.goto.find", "Ctrl+F", SWT.MOD1 | 'F', find);
	}

	@Override
	public String getId() {
		return "menu.goto";
	}
}
