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
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
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
		if (shell != null) {
			// close the same window if it is already open ('scanning incomplete')
			shell.close();
			shell.dispose();
		}
		
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay.getShells()[0];
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.statistics"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 15));
		
		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(0), null));
		iconLabel.setImage(parent.getImage());
		shell.setImage(parent.getImage());
		
		Label titleLabel = new Label(shell, SWT.NONE);
		FontData sysFontData = currentDisplay.getSystemFont().getFontData()[0];
		titleLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(iconLabel), null, new FormAttachment(0), null));
		titleLabel.setFont(new Font(null, sysFontData.getName(), sysFontData.getHeight()+3, sysFontData.getStyle() | SWT.BOLD));
		titleLabel.setText(Labels.getLabel(scanningResults.getScanInfo().isCompletedNormally() ? "text.scan.completed" : "text.scan.incomplete"));
			
		Text statsText = new Text(shell, SWT.MULTI | SWT.READ_ONLY);
		statsText.setBackground(currentDisplay.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		statsText.setLayoutData(LayoutHelper.formData(new FormAttachment(iconLabel), null, new FormAttachment(titleLabel), null));
		statsText.setText(prepareText(scanningResults.getScanInfo()));
		statsText.pack();
		
		Button button = createCloseButton();
		Point buttonSize = button.getSize();
		button.setLayoutData(LayoutHelper.formData(buttonSize.x, buttonSize.y, null, new FormAttachment(statsText, 30, SWT.RIGHT), new FormAttachment(statsText), null));
		
		shell.layout();
		shell.pack();
	}

	private String prepareText(ScanInfo scanInfo) {
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
