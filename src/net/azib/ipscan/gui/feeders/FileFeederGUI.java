/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FileFeeder;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * GUI for initialization of FileFeeder.
 *
 * @author Anton Keks
 */
public class FileFeederGUI extends AbstractFeederGUI {

	private Label fileNameLabel;
	private Text fileNameText;
	
	private Button browseButton;

	public FileFeederGUI(Composite parent) {
		super(parent);
	}

	protected void initialize() {
		feeder = new FileFeeder();
		
		setLayout(LayoutHelper.formLayout(3, 3, 4));
		
        fileNameLabel = new Label(this, SWT.NONE);
        fileNameText = new Text(this, SWT.BORDER);
        browseButton = new Button(this, SWT.NONE);
        
        fileNameLabel.setText(getStringLabel("name"));
        FormData formData = new FormData();
        formData.left = new FormAttachment(0);
        formData.top = new FormAttachment(fileNameText, 0, SWT.CENTER);
        formData.bottom = new FormAttachment(browseButton, 0, SWT.BOTTOM);
        fileNameLabel.setLayoutData(formData);
        
        // some long text
        fileNameText.setText("255.255.255.255.xxx.xxx");
		formData = new FormData(fileNameText.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, SWT.DEFAULT);
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(fileNameLabel);
        fileNameText.setLayoutData(formData);
        fileNameText.setText("");
        
        browseButton.setText(getStringLabel("browse"));
        formData = new FormData();
        formData.top = new FormAttachment(0);
        formData.bottom = new FormAttachment(fileNameText, 0, SWT.BOTTOM);
        formData.left = new FormAttachment(fileNameText);
        browseButton.setLayoutData(formData);
        browseButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setText(getStringLabel("browse"));
				String fileName = dialog.open();
				if (fileName != null) {
					fileNameText.setText(fileName);
					fileNameText.setSelection(fileName.length());
				}
			}
        	
        });
                        
		pack();
	}

	public Feeder getFeeder() {
		((FileFeeder)feeder).initialize(fileNameText.getText());
		return feeder;
	}
	
	public String serialize() {
		return fileNameText.getText();
	}

	public void unserialize(String serialized) {
		fileNameText.setText(serialized);
	}
	
}
