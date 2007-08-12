/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.core.ScanningResultList.ScanInfo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * StatisticsDialog - shows statistical information about the last scan
 *
 * @author Anton Keks
 */
public class StatisticsDialog extends AbstractModalDialog {
	
	private ScanningResultList scanningResults;
	
	public StatisticsDialog(ScanningResultList scanningResultList) {
		this.scanningResults = scanningResultList;
	}
	
	@Override
	public void open() {
		if (scanningResults.areResultsAvailable()) {
			createShell();
			super.open();
		}
		else {
			throw new UserErrorException("commands.noResults");
		}
	}
	
	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay.getShells()[0];
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.statistics"));
		shell.setSize(new Point(360, 240));
		
		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setLocation(10, 10);
		iconLabel.setImage(parent.getImage());
		shell.setImage(parent.getImage());
		iconLabel.pack();
		int leftBound = iconLabel.getBounds().width + 25;
		
		Label titleLabel = new Label(shell, SWT.NONE);
		FontData sysFontData = currentDisplay.getSystemFont().getFontData()[0];
		titleLabel.setLocation(leftBound, 10);
		titleLabel.setFont(new Font(null, sysFontData.getName(), sysFontData.getHeight()+3, sysFontData.getStyle() | SWT.BOLD));
		titleLabel.setText(Labels.getLabel(scanningResults.getScanInfo().isFinished() ? "text.scan.finished" : "text.scan.incomplete"));
		titleLabel.pack();
		
		Button button = createCloseButton();
		
		ScanInfo scanInfo = scanningResults.getScanInfo();
		
		String ln = System.getProperty("line.separator");
		StringBuilder text = new StringBuilder();
		text.append(Labels.getLabel("text.scan.time.total"))
			.append(timeToText(scanInfo.getScanTime())).append(ln);
		text.append(Labels.getLabel("text.scan.time.average"))
			.append(timeToText((double)scanInfo.getScanTime() / scanInfo.getHostCount())).append(ln);
		
		text.append(ln).append(scanningResults.getFeederName()).append(ln)
			.append(scanningResults.getFeederInfo()).append(ln).append(ln);
		
		text.append(Labels.getLabel("text.scan.hosts.total")).append(scanInfo.getHostCount()).append(ln);
		if (scanInfo.getAliveCount() > 0) 
			text.append(Labels.getLabel("text.scan.hosts.alive")).append(scanInfo.getAliveCount()).append(ln);
		if (scanInfo.getWithPortsCount() > 0) 
			text.append(Labels.getLabel("text.scan.hosts.ports")).append(scanInfo.getWithPortsCount()).append(ln);
		
		Text statsText = new Text(shell, SWT.MULTI | SWT.READ_ONLY);
		statsText.setBackground(currentDisplay.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		statsText.setBounds(leftBound, titleLabel.getBounds().y + 30, shell.getClientArea().width - leftBound - 10, button.getLocation().y - 50);
		statsText.setText(text.toString());
	}
	
	/**
	 * @param scanTime in milliseconds
	 * @return provided time in human-readable form
	 */
	static String timeToText(double scanTime) {
		double seconds = scanTime/1000;
		NumberFormat format = new DecimalFormat("#.#");
		if (seconds >= 60)
			return format.format(seconds/60) + Labels.getLabel("text.scan.time.minutes");
		return format.format(seconds) + Labels.getLabel("text.scan.time.seconds");
	}

}
