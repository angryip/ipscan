/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials  
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * 	   Anton Keks - Adaptation for Angry IP Scanner
 *******************************************************************************/
package net.azib.ipscan.gui.mac;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.AboutDialog;
import net.azib.ipscan.gui.PreferencesDialog;
import net.azib.ipscan.gui.SelectFetchersDialog;
import net.azib.ipscan.gui.actions.HelpActions.CheckVersion;

import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.carbon.*;
import org.eclipse.swt.widgets.Display;
import org.picocontainer.Startable;

/**
 * Mac-specific application menu handler
 * in order to conform better to Mac standards.
 */
public class MacApplicationMenu implements Startable {
	
	private static final int kHICommandAbout = ('a' << 24) + ('b' << 16) + ('o' << 8) + 'u';
	private static final int kHICommandPreferences = ('p' << 24) + ('r' << 16) + ('e' << 8) + 'f';
	private static final int kHICommandFetchers = ('f' << 24) + ('e' << 16) + ('t' << 8) + 'c';
	private static final int kHICommandCheckVersion = ('v' << 24) + ('e' << 16) + ('r' << 8) + 's';

	private final AboutDialog aboutDialog;
	private final PreferencesDialog preferencesDialog;
	private final SelectFetchersDialog selectFetchersDialog;
	private final CheckVersion checkVersionListener;

	public MacApplicationMenu(AboutDialog aboutDialog, PreferencesDialog preferencesDialog, SelectFetchersDialog selectFetchersDialog, CheckVersion checkVersionListener) {
		this.aboutDialog = aboutDialog;
		this.preferencesDialog = preferencesDialog;
		this.selectFetchersDialog = selectFetchersDialog;
		this.checkVersionListener = checkVersionListener;
	}

	public void start() {
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				hookApplicationMenu(display);
			}
		});
	}

	public void stop() {
	}

	/**
	 * See Apple Technical Q&A 1079 (http://developer.apple.com/qa/qa2001/qa1079.html)<br/>
	 * Also
	 * http://developer.apple.com/documentation/Carbon/Reference/Menu_Manager/menu_mgr_ref/function_group_10.html
	 */
	public void hookApplicationMenu(final Display display) {
		// callback target
		Object target = new Object() {
			@SuppressWarnings("unused")
			int commandProc(int nextHandler, int theEvent, int userData) {
				if (OS.GetEventKind(theEvent) == OS.kEventProcessCommand) {
					HICommand command = new HICommand();
					OS.GetEventParameter(theEvent, OS.kEventParamDirectObject, OS.typeHICommand, null, HICommand.sizeof, null, command);
					switch (command.commandID) {
						case kHICommandPreferences: 
							preferencesDialog.open();
							return OS.noErr;
						case kHICommandFetchers:
							selectFetchersDialog.open();
							return OS.noErr;
						case kHICommandAbout:
							aboutDialog.open();
							return OS.noErr;
						case kHICommandCheckVersion:
							checkVersionListener.check();
							return OS.noErr;
					}
				}
				return OS.eventNotHandledErr;
			}
		};
		final Callback commandCallback = new Callback(target, "commandProc", 3); 
		int commandProc = commandCallback.getAddress();
		if (commandProc == 0) {
			commandCallback.dispose();
			return; // give up
		}

		// install event handler for commands
		int[] mask = new int[] {OS.kEventClassCommand, OS.kEventProcessCommand};
		OS.InstallEventHandler(OS.GetApplicationEventTarget(), commandProc, mask.length / 2, mask, 0, null);

		// create menu commands
		int[] outMenu = new int[1];
		short[] outIndex = new short[1];
		if (OS.GetIndMenuItemWithCommandID(0, kHICommandPreferences, 1, outMenu, outIndex) == OS.noErr && outMenu[0] != 0) {
			int menu = outMenu[0];

			String aboutName = Labels.getLabel("title.about") + " " + Version.NAME;
			char buf[] = new char[aboutName.length()];
			aboutName.getChars(0, buf.length, buf, 0);
			int str = OS.CFStringCreateWithCharacters(OS.kCFAllocatorDefault, buf, buf.length);
			OS.InsertMenuItemTextWithCFString(menu, str, (short) 0, 0, kHICommandAbout);
			OS.CFRelease(str);
			// add separator between About & Preferences
			OS.InsertMenuItemTextWithCFString(menu, 0, (short) 1, OS.kMenuItemAttrSeparator, 0);

			// enable Preferences menu
			OS.EnableMenuCommand(menu, kHICommandPreferences);

			// add Fetchers menu
			String fetchersName = Labels.getLabel("menu.tools.fetchers").replace("&", "");
			buf = new char[fetchersName.length()];
			fetchersName.getChars(0, buf.length, buf, 0);
			str = OS.CFStringCreateWithCharacters(OS.kCFAllocatorDefault, buf, buf.length);
			OS.InsertMenuItemTextWithCFString(menu, str, (short) 3, 0, kHICommandFetchers);
			OS.CFRelease(str);
			// add separator between Fetchers & Check Version
			OS.InsertMenuItemTextWithCFString(menu, 0, (short) 4, OS.kMenuItemAttrSeparator, 0);
			
			// add Check Version menu
			String checkVersionName = Labels.getLabel("menu.help.checkVersion").replace("&", "");
			buf = new char[checkVersionName.length()];
			checkVersionName.getChars(0, buf.length, buf, 0);
			str = OS.CFStringCreateWithCharacters(OS.kCFAllocatorDefault, buf, buf.length);
			OS.InsertMenuItemTextWithCFString(menu, str, (short) 5, 0, kHICommandCheckVersion);
			OS.CFRelease(str);
		}

		// schedule disposal of callback object
		display.disposeExec(new Runnable() {
			public void run() {
				commandCallback.dispose();
			}
		});
	}
}
