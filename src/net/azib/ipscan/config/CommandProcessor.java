/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.config;

/**
 * Interface for providing of various commands to the application
 *
 * @author Anton Keks
 */
public interface CommandProcessor {
	boolean shouldAutoStart();
	boolean shouldAutoQuit();
}
