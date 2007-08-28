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
import net.azib.ipscan.gui.AboutDialog;
import net.azib.ipscan.gui.OptionsDialog;

import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.carbon.*;
import org.eclipse.swt.widgets.Display;

/**
 * CarbonUIEnhancer - adds some menu items to Mac's application menu,
 * in order to conform better to Mac standards.
 * 
 * TODO: test this code on a real Mac
 */
public class CarbonUIEnhancer {
	
	private static final int kHICommandPreferences = ('p' << 24) + ('r' << 16) + ('e' << 8) + 'f';
	private static final int kHICommandAbout = ('a' << 24) + ('b' << 16) + ('o' << 8) + 'u';

	private final AboutDialog aboutDialog;
	private final OptionsDialog optionsDialog;

	public CarbonUIEnhancer(AboutDialog aboutDialog, OptionsDialog optionsDialog) {
		this.aboutDialog = aboutDialog;
		this.optionsDialog = optionsDialog;
	}

	public void startup() {
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				hookApplicationMenu(display);
			}
		});
	}

	/**
	 * See Apple Technical Q&A 1079 (http://developer.apple.com/qa/qa2001/qa1079.html)<br />
	 * Also
	 * http://developer.apple.com/documentation/Carbon/Reference/Menu_Manager/menu_mgr_ref/function_group_10.html
	 */
	public void hookApplicationMenu(final Display display) {
		// Callback target
		Object target = new Object() {
			@SuppressWarnings("unused")
			int commandProc(int nextHandler, int theEvent, int userData) {
				if (OS.GetEventKind(theEvent) == OS.kEventProcessCommand) {
					HICommand command = new HICommand();
					OS.GetEventParameter(theEvent, OS.kEventParamDirectObject, OS.typeHICommand, null, HICommand.sizeof, null, command);
					switch (command.commandID) {
						case kHICommandPreferences: {
							optionsDialog.open();
							return OS.noErr;
						}
						case kHICommandAbout:
							aboutDialog.open();
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
		int[] mask = new int[] { OS.kEventClassCommand, OS.kEventProcessCommand };
		OS.InstallEventHandler(OS.GetApplicationEventTarget(), commandProc, mask.length / 2, mask, 0, null);

		// create About menu command
		int[] outMenu = new int[1];
		short[] outIndex = new short[1];
		if (OS.GetIndMenuItemWithCommandID(0, kHICommandPreferences, 1, outMenu, outIndex) == OS.noErr && outMenu[0] != 0) {
			int menu = outMenu[0];
			
			String aboutName = Labels.getLabel("menu.help.about");

			int l = aboutName.length();
			char buffer[] = new char[l];
			aboutName.getChars(0, l, buffer, 0);
			int str = OS.CFStringCreateWithCharacters(OS.kCFAllocatorDefault, buffer, l);
			OS.InsertMenuItemTextWithCFString(menu, str, (short) 0, 0, kHICommandAbout);
			OS.CFRelease(str);
			// add separator between About & Preferences
			OS.InsertMenuItemTextWithCFString(menu, 0, (short) 1, OS.kMenuItemAttrSeparator, 0);

			// enable pref menu
			OS.EnableMenuCommand(menu, kHICommandPreferences);
		}

		// schedule disposal of callback object
		display.disposeExec(new Runnable() {
			public void run() {
				commandCallback.dispose();
			}
		});
	}

}
