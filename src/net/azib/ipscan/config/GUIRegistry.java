package net.azib.ipscan.config;

import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.di.Injector;
import net.azib.ipscan.feeders.FeederRegistry;
import net.azib.ipscan.gui.SWTAwareStateMachine;
import net.azib.ipscan.gui.feeders.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

public class GUIRegistry {
	public void register(Injector i) {
		var display = Display.getDefault();
		i.register(Display.class, display);
		i.register(GUIConfig.class, Config.getConfig().forGUI());

		var shell = new Shell();
		i.register(Shell.class, shell);
		i.register(Menu.class, new Menu(shell, SWT.BAR));
		i.register(FeederSelectionCombo.class, new FeederSelectionCombo(i.require(ControlsArea.class)));
		i.register(Button.class, new Button(i.require(ControlsArea.class), SWT.NONE));

		var stateMachine = new SWTAwareStateMachine(display);
		i.register(SWTAwareStateMachine.class, stateMachine);
		i.register(StateMachine.class, stateMachine);
		i.register(RangeFeederGUI.class, RandomFeederGUI.class, FileFeederGUI.class);

		i.register(FeederRegistry.class, i.require(FeederGUIRegistry.class));
	}
}
