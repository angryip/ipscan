/**
 * 
 */
package net.azib.ipscan.gui;

import java.io.File;
import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.OpenersConfig;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * EditOpenersDialog
 *
 * @author anton
 */
public class EditOpenersDialog extends AbstractModalDialog {

	private FetcherRegistry fetcherRegistry;
	private List openersList;
	private Group editFieldsGroup;
	private Text openerNameText;
	private Text openerStringText;
	private Text workingDirText;
	private Button isInTerminalCheckbox;
	private int currentSelectionIndex;
	
	public EditOpenersDialog(FetcherRegistry fetcherRegistry) {
		this.fetcherRegistry = fetcherRegistry;
		createShell();
	}
	
	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.openers.edit"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));		
		
		Label messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setText(Labels.getLabel("text.openers.edit"));
		
		openersList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		editFieldsGroup = new Group(shell, SWT.NONE);

		openersList.setLayoutData(LayoutHelper.formData(135, 200, null, null, new FormAttachment(messageLabel), new FormAttachment(editFieldsGroup, 0, SWT.BOTTOM)));
		for (Iterator i = Config.getOpenersConfig().iterateNames(); i.hasNext();) {
			String name = (String) i.next();
			openersList.add(name);
		}
		openersList.addListener(SWT.Selection, new ItemSelectListener());
		
		Button upButton = new Button(shell, SWT.NONE);
		upButton.setText(Labels.getLabel("button.up"));
		upButton.addListener(SWT.Selection, new UpButtonListener(openersList));
		
		Button downButton = new Button(shell, SWT.NONE);
		downButton.setText(Labels.getLabel("button.down"));	
		downButton.addListener(SWT.Selection, new DownButtonListener(openersList));
		
		Button addButton = new Button(shell, SWT.NONE);
		addButton.setText(Labels.getLabel("button.add"));
		addButton.addListener(SWT.Selection, new AddButtonListener());

		Button deleteButton = new Button(shell, SWT.NONE);
		deleteButton.setText(Labels.getLabel("button.delete"));
		deleteButton.addListener(SWT.Selection, new DeleteButtonListener());

		upButton.setLayoutData(LayoutHelper.formData(new FormAttachment(openersList), new FormAttachment(deleteButton, 0, SWT.RIGHT), new FormAttachment(messageLabel), null));
		downButton.setLayoutData(LayoutHelper.formData(new FormAttachment(openersList), new FormAttachment(deleteButton, 0, SWT.RIGHT), new FormAttachment(upButton), null));
		addButton.setLayoutData(LayoutHelper.formData(new FormAttachment(openersList), new FormAttachment(deleteButton, 0, SWT.RIGHT), new FormAttachment(downButton, 16), null));	
		deleteButton.setLayoutData(LayoutHelper.formData(new FormAttachment(openersList), null, new FormAttachment(addButton), null));
						
		editFieldsGroup.setLayoutData(LayoutHelper.formData(new FormAttachment(upButton, 10), null, new FormAttachment(messageLabel), null));
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.fill = true;
		rowLayout.justify = true; 
		rowLayout.marginTop = 13;
		editFieldsGroup.setLayout(rowLayout);
		
		Label openerNameLabel = new Label(editFieldsGroup, SWT.NONE);
		openerNameLabel.setText(Labels.getLabel("text.openers.name"));
		openerNameLabel.setSize(SWT.DEFAULT, 18);
		openerNameText = new Text(editFieldsGroup, SWT.BORDER);
		openerNameText.setSize(SWT.DEFAULT, 22);
		openerNameText.addListener(SWT.KeyUp, new OpenerNameChange());

		isInTerminalCheckbox = new Button(editFieldsGroup, SWT.CHECK);
		isInTerminalCheckbox.setText(Labels.getLabel("text.openers.inTerminal"));
		isInTerminalCheckbox.setSize(SWT.DEFAULT, 18);

		Label openerStringLabel = new Label(editFieldsGroup, SWT.NONE);
		openerStringLabel.setText(Labels.getLabel("text.openers.string"));
		openerStringLabel.setSize(SWT.DEFAULT, 18);
		openerStringText = new Text(editFieldsGroup, SWT.BORDER);
		openerStringText.setSize(SWT.DEFAULT, 22);
		
		Button hintButton = new Button(editFieldsGroup, SWT.NONE);
		hintButton.setText(Labels.getLabel("text.openers.hint"));
		hintButton.addListener(SWT.Selection, new HintButtonListener());
		
		Label openerDirLabel = new Label(editFieldsGroup, SWT.NONE);
		openerDirLabel.setText(Labels.getLabel("text.openers.directory"));
		openerDirLabel.setSize(SWT.DEFAULT, 18);
		workingDirText = new Text(editFieldsGroup, SWT.BORDER);
		workingDirText.setSize(SWT.DEFAULT, 22);
				
		editFieldsGroup.layout();
		editFieldsGroup.pack();

		// no close button on Mac
		if (!Platform.MAC_OS) {
			Button closeButton = new Button(shell, SWT.NONE);
			closeButton.setText(Labels.getLabel("button.close"));
			closeButton.setLayoutData(LayoutHelper.formData(85, SWT.DEFAULT, null, new FormAttachment(editFieldsGroup, 0, SWT.RIGHT), new FormAttachment(editFieldsGroup, 6), null));
			closeButton.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					shell.close();
				}
			});
		}
			
		shell.pack();

		openersList.select(0);
		loadFieldsForSelection();
		
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event e) {
				saveOpeners();
			}
		});
	}
	
	private void saveOpeners() {
		// save any possible changes in text boxes
		saveCurrentFields();			

		// now save everything else (order, etc)
		OpenersConfig openersConfig = Config.getOpenersConfig();
		openersConfig.update(openersList.getItems());
		openersConfig.store();
	}

	private void saveCurrentFields() {
		String openerName = openerNameText.getText();
		if (openerName.length() == 0)
			return;
		
		File workingDir = workingDirText.getText().length() > 0 ? new File(workingDirText.getText()) : null;
		Config.getOpenersConfig().add(openerName, new OpenersConfig.Opener(openerStringText.getText(), isInTerminalCheckbox.getSelection(), workingDir));
		openersList.setItem(currentSelectionIndex, openerName);
	}
	
	private void loadFieldsForSelection() {
		currentSelectionIndex = openersList.getSelectionIndex();
		String openerName = openersList.getItem(currentSelectionIndex);
		editFieldsGroup.setText(openerName);
		Opener opener = Config.getOpenersConfig().getOpener(openerName);
		openerNameText.setText(openerName);
		openerStringText.setText(opener.execString);
		workingDirText.setText(opener.workingDir != null ? opener.workingDir.toString() : "");
		isInTerminalCheckbox.setSelection(opener.inTerminal);
	}

	private class HintButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			// compose the message with all available fetchers
			StringBuffer message = new StringBuffer(Labels.getLabel("text.openers.hintText"));
			for (Iterator i = fetcherRegistry.getSelectedFetchers().iterator(); i.hasNext(); ) {
				String fetcherLabel = ((Fetcher)i.next()).getLabel();
				message.append("${").append(fetcherLabel).append("}   - ").append(Labels.getLabel(fetcherLabel)).append('\n');
			}
			
			MessageBox mb = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			mb.setText(Labels.getLabel("title.openers.edit"));
			mb.setMessage(message.toString());
			mb.open();
		}
	}
	
	private class DeleteButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			int firstIndex = openersList.getSelectionIndex();
			openersList.remove(openersList.getSelectionIndices());
			openersList.setSelection(firstIndex);
			loadFieldsForSelection();
		}
	}

	private class AddButtonListener implements Listener {
		
		public void handleEvent(Event event) {
			saveCurrentFields();
			
			currentSelectionIndex = openersList.getSelectionIndex();
			if (currentSelectionIndex < 0) {
				currentSelectionIndex = openersList.getItemCount();
			}
			String newName = Labels.getLabel("text.openers.new");
			openersList.add(newName, currentSelectionIndex);
			openersList.setSelection(currentSelectionIndex);
			
			// reset fields
			editFieldsGroup.setText(newName);
			openerNameText.setText(newName);
			openerStringText.setText("${fetcher.ip}");
			workingDirText.setText("");
			isInTerminalCheckbox.setSelection(false);
			
			openerNameText.forceFocus();
			openerNameText.setSelection(0, newName.length());
		}
	}

	private class ItemSelectListener implements Listener {
		
		public void handleEvent(Event event) {
			if (openersList.getSelectionCount() == 0)
				return;
			
			saveCurrentFields();
			loadFieldsForSelection();
		}
	}
	
	private class OpenerNameChange implements Listener {

		public void handleEvent(Event event) {
			String name = openerNameText.getText();
			editFieldsGroup.setText(name);
			openersList.setItem(currentSelectionIndex, name);
		}
	}

}
