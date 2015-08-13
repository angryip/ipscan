/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningResultList.ScanInfo;
import net.azib.ipscan.core.UserErrorException;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * StatisticsDialog - shows statistical information about the last scan
 *
 * @author Anton Keks
 */
public class StatisticsDialog extends InfoDialog {
	
	private final ScanningResultList scanningResults;

	@Inject public StatisticsDialog(ScanningResultList scanningResults) {
		super(Labels.getLabel("title.statistics"), null);
		this.scanningResults = scanningResults;
	}
	
	@Override
	public void open() {
		if (scanningResults.isInfoAvailable()) {
			setMessage(prepareText());
			
			if (shell != null) {
				// close the same window if it is already open ('scanning incomplete')
				shell.close();
				shell.dispose();
			}

			super.open();
		}
		else {
			throw new UserErrorException("commands.noResults");
		}
	}

	String prepareText() {
		ScanInfo scanInfo = scanningResults.getScanInfo();
		title2 = Labels.getLabel(scanInfo.isCompletedNormally() ? 
				"text.scan.completed" : "text.scan.incomplete");
				
		String ln = System.getProperty("line.separator");
		StringBuilder text = new StringBuilder();
		text.append(Labels.getLabel("text.scan.time.total"))
			.append(timeToText(scanInfo.getScanTime())).append(ln);
		text.append(Labels.getLabel("text.scan.time.average"))
			.append(timeToText((double)scanInfo.getScanTime() / scanInfo.getHostCount())).append(ln);
		
		text.append(ln).append(scanningResults.getFeederName()).append(ln)
			.append(scanningResults.getFeederInfo()).append(ln).append(ln);
		
		text.append(Labels.getLabel("text.scan.hosts.total")).append(scanInfo.getHostCount()).append(ln);
		text.append(Labels.getLabel("text.scan.hosts.alive")).append(scanInfo.getAliveCount()).append(ln);
		if (scanInfo.getWithPortsCount() > 0) 
			text.append(Labels.getLabel("text.scan.hosts.ports")).append(scanInfo.getWithPortsCount()).append(ln);
		return text.toString();
	}
	
	/**
	 * @param scanTime in milliseconds
	 * @return provided time in human-readable form
	 */
	static String timeToText(double scanTime) {
		double totalSeconds = scanTime/1000;
		double totalMinutes = totalSeconds/60;
		double totalHours = totalMinutes/60;
		NumberFormat format = new DecimalFormat("#.#");
		if (totalHours >= 1)
			return format.format(totalHours) + Labels.getLabel("unit.hour");
		if (totalMinutes >= 1)
			return format.format(totalMinutes) + Labels.getLabel("unit.minute");
		return format.format(totalSeconds) + Labels.getLabel("unit.second");
	}

}
